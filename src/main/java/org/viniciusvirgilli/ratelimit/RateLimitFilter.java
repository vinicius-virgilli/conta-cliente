package org.viniciusvirgilli.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Map<String, Long> ipsBloqueados = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!enable) {
            return;
        }

        String ip = extraiIp(requestContext);

        if (isIpBloqueado(ip)) {
            requestContext.abortWith(respostaToManyRequests(ip));
            return;
        }

        Bucket bucket = buckets.computeIfAbsent(ip, this::criaBuckets);

        if (!bucket.tryConsume(1)) { ipsBloqueados.put(ip, System.currentTimeMillis() + minutosBloqueio*60000);
            requestContext.abortWith(respostaToManyRequests(ip)); }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (!enable) return;

        String ip = extraiIp(requestContext);
        Bucket bucket = buckets.get(ip);
        if (bucket != null) {
            long requestsDisponiveis = bucket.getAvailableTokens();

            responseContext.getHeaders().add("X-RateLimit-Available", requestsDisponiveis);
            responseContext.getHeaders().add("X-RateLimit-Limits",
                    String.format("%d/s, %d/m, %d/h",
                            requestsPorSegundo, requestsPorMinuto, requestsPorHora));
        }
    }

    private Bucket criaBuckets(String ip) {
        Bandwidth limitePorSegundo = Bandwidth.simple(requestsPorSegundo, Duration.ofSeconds(1));
        Bandwidth limitePorMinuto = Bandwidth.simple(requestsPorMinuto, Duration.ofMinutes(1));
        Bandwidth limitePorHora = Bandwidth.simple(requestsPorHora, Duration.ofHours(1));

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
            ipsBloqueados.remove(ip);
            return false;
        }

        return true;
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
                        "bloqueadoAteHorario", bloqueadoAteHorario.toString()
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
