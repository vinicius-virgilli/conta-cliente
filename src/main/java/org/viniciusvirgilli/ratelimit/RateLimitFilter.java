package org.viniciusvirgilli.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.VerboseResult;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.sql.Ref;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Provider
@ApplicationScoped
@Startup
public class RateLimitFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @ConfigProperty(name = "ratelimit.enable", defaultValue = "true")
    boolean enable;

    @ConfigProperty(name = "ratelimit.requests.por.segundo", defaultValue = "200")
    long requestsPorSegundo;

    @ConfigProperty(name = "ratelimit.requests.por.minuto", defaultValue = "1000")
    long requestsPorMinuto;

    @ConfigProperty(name = "ratelimit.requests.por.hora", defaultValue = "6000")
    long requestsPorHora;

    @ConfigProperty(name = "ratelimit.minutos.bloqueio", defaultValue = "1")
    long minutosBloqueio;

    // bucket = balde
    private final Cache<String, Bucket> baldes = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofDays(1))
            .build();

    private final Map<String, Long> ipsBloqueados = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        log.info(System.lineSeparator());
        log.debug("[Inicio filtro] ipsBloqueados.size={}, baldes.size={}", ipsBloqueados.size(), baldes.estimatedSize());

        if (!enable) {
            return;
        }

        String ip = extraiIp(requestContext);
        log.debug("Requisicao recebida de IP: {}", ip);


        if (isIpBloqueado(ip)) {
            log.info("Requisicao abortada: IP {} ainda bloqueado", ip);
            requestContext.abortWith(respostaToManyRequests(ip));
            return;
        }

        Bucket balde = baldes.get(ip, k -> criaBalde());

        boolean consumiu = balde.tryConsume(1);

        if (!consumiu) {
            long instanteDesbloqueio = System.currentTimeMillis() + minutosBloqueio*60000;

            ipsBloqueados.put(ip, instanteDesbloqueio);
            log.info("Requisicao abortada: IP {} excedeu o limite de requisicoes", ip);
            requestContext.abortWith(respostaToManyRequests(ip));
        } else {
            log.info("[Ratelimit] - IP: {} consumiu 1 token. Tokens restantes (aprox): {}", ip, balde.getAvailableTokens());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (!enable) return;

        String ip = extraiIp(requestContext);
        Bucket balde = baldes.get(ip, k -> criaBalde());

        if (balde != null) {
            populaHeadersResponse(responseContext, balde);
        }
    }

    private void populaHeadersResponse(ContainerResponseContext responseContext, Bucket balde) {
        VerboseResult<Long> verbose = balde.asVerbose().getAvailableTokens();
        long[] disponiveisPorBanda = verbose.getDiagnostics().getAvailableTokensPerEachBandwidth();
        Bandwidth[] bandas = verbose.getConfiguration().getBandwidths();

        if (disponiveisPorBanda.length> 0) {
            responseContext.getHeaders().add("X-RateLimit-available-Second", String.valueOf(disponiveisPorBanda[0]));
            responseContext.getHeaders().add("X-RateLimit-Limit-Second", String.valueOf(bandas[0].getCapacity()));
        }
        if (disponiveisPorBanda.length> 1) {
            responseContext.getHeaders().add("X-RateLimit-available-Minute", String.valueOf(disponiveisPorBanda[1]));
            responseContext.getHeaders().add("X-RateLimit-Limit-Minute", String.valueOf(bandas[1].getCapacity()));
        }
        if (disponiveisPorBanda.length> 2) {
            responseContext.getHeaders().add("X-RateLimit-available-Hour", String.valueOf(disponiveisPorBanda[2]));
            responseContext.getHeaders().add("X-RateLimit-Limit-Hour", String.valueOf(bandas[2].getCapacity()));
        }
    }

    private Bucket criaBalde() {
        Bandwidth limitePorSegundo = Bandwidth.classic(
                requestsPorSegundo,
                Refill.intervally(requestsPorSegundo, Duration.ofSeconds(1)));
        Bandwidth limitePorMinuto = Bandwidth.classic(
                requestsPorMinuto,
                Refill.intervally(requestsPorMinuto, Duration.ofMinutes(1)));
        Bandwidth limitePorHora = Bandwidth.classic(
                requestsPorHora,
                Refill.intervally(requestsPorHora, Duration.ofHours(1)));

        return Bucket.builder()
                .addLimit(limitePorSegundo)
                .addLimit(limitePorMinuto)
                .addLimit(limitePorHora)
                .build();
    }

    private boolean isIpBloqueado(String ip) {
        Long instanteDeDesbloqueio = ipsBloqueados.get(ip);
        if (instanteDeDesbloqueio == null) return false;

        if (System.currentTimeMillis() < instanteDeDesbloqueio) {
            return true;
        }

        ipsBloqueados.remove(ip);
        log.info("[Ratelimit] IP: {} desbloqueado (balde recarregado com {} tokens/s)", ip, requestsPorSegundo);

        return false;
    }

    private Response respostaToManyRequests(String ip) {
        long bloqueadoAteInstante = ipsBloqueados.get(ip);

        LocalDateTime bloqueadoAteHorario = Instant.ofEpochMilli(bloqueadoAteInstante)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity(Map.of(
                        "Erro", "Limite de requisições excedito",
                        "mensagem", "Aguarde " + minutosBloqueio + " minuto(s) antes de tentar novamente",
                        "bloqueadoAteInstante", bloqueadoAteInstante,
                        "bloqueadoAteHorario", bloqueadoAteHorario.toString(),
                        "ip", ip
                )).build();
    }

    private String extraiIp(ContainerRequestContext context) {
        String ip = context.getHeaderString("X-Forwarded-For");
        if (ip == null) {
            ip = context.getUriInfo().getRequestUri().getHost();
        }

        if (ip != null) return ip;

        return "desconhecido";
    }


}
