/**
 * use {@link  org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory}
 * <pre>
 *      spring:
 *          cloud:
 *              gateway:
 *                  discovery.locator:
 *                      enabled: true
 *                      filters:
 *                          - name: RequestRateLimiter
 *  </pre>
 */
package cn.monkey.gateway.stream.ratelimit.async;