/**
 * <p>
 * 使用ChannelHandler实现黑名单功能
 * <ul>
 *     优点：
 *
 * <li>1、能够在识别出http报文时，及时做出响应，切断网络，避免事件继续在ChannelHandler中传播，查看：
 * {@link cn.monkey.gateway.stream.blacklist.sync.BlackListCustomizer}
 *  <pre>
 *      {@code
 *      public HttpServer apply(HttpServer httpServer) {
 *         ChannelHandler channelHandler;
 *         try {
 *             channelHandler = this.blackListChannelHandlerFactory.getObject();
 *         } catch (Exception e) {
 *             log.warn("blackList channelHandler create error:\n", e);
 *             return httpServer;
 *         }
 *         // add after httpClientCodec
 *         return httpServer.doOnChannelInit((connectionObserver, channel, remoteAddress) -> channel.pipeline().addAfter("reactor.left.httpCodec", "blackListChannelHandler", channelHandler));
 *     }}
 *  </pre>
 * </li>
 * </ul>
 * <ul>
 *     缺点：
 * <li>1、基于netty的线程模型，在channelHandler中BIO的操作会影响到workers ({@link io.netty.channel.EventLoopGroup})
 *     线程执行效率：{@link io.netty.channel.ChannelOutboundHandler#write(io.netty.channel.ChannelHandlerContext, Object, io.netty.channel.ChannelPromise)}。
 *      通常情况下，避免workers线程中出现BIO操作。以提高系统的吞吐量。<br><br/>
 *      可能造成BIO操作的地方有：
 *          <ul>
 *              <li>{@link cn.monkey.gateway.stream.blacklist.sync.BlackEntityRepository#add(BlackEntity)}</li>
 *              <li>{@link cn.monkey.gateway.stream.blacklist.sync.BlackEntityRepository#containsKey(String)}</li>
 *              <li>{@link cn.monkey.gateway.stream.blacklist.sync.FailCounter#incrementAndGet(String)}</li>
 *          </ul>
 * </li>
 * </ul>
 * </p>
 */
package cn.monkey.gateway.stream.blacklist.sync;

import cn.monkey.gateway.stream.blacklist.BlackEntity;