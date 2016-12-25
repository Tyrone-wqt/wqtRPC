import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by lenovo on 2016/12/23.
 */
public class RpcServer {
    private int port = 8989;
    private String serverAddress = "127.0.0.1";

    public static void main(String[] args) {
        RpcServer server = new RpcServer();
        server.bind();
    }

    public void bind() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 100);
            bootstrap.childHandler(new ChannelInitializer<NioServerSocketChannel>() {
                @Override
                protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
                    ChannelPipeline pipeline = nioServerSocketChannel.pipeline();
                    pipeline.addLast(new RpcDecoder(RpcRequest.class));
                    pipeline.addLast(new RpcEncoder(RpcResponse.class));
                }
            });
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(serverAddress, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
