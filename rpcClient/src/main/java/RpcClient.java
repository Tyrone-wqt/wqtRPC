import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by lenovo on 2016/12/24.
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    private int serverPort = 8989;
    private String serverAddress = "127.0.0.1";
    private Channel clientChannel;
    private RpcResponse rpcResponse;

    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 客户端 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    //pipeline.addLast(new RpcEncoder(RpcRequest.class)); // 编码 RPC 请求
                    //pipeline.addLast(new RpcDecoder(RpcResponse.class)); // 解码 RPC 响应
                    pipeline.addLast(RpcClient.this); // 处理 RPC 响应
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 连接 RPC 服务器
            ChannelFuture future = bootstrap.connect(serverAddress, serverPort).sync();
            // 写入 RPC 请求数据并关闭连接
            Channel channel = future.channel();
            this.clientChannel = channel;
            //channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            // 返回 RPC 响应对象
            //return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public RpcResponse send(RpcRequest rpcRequest) throws InterruptedException {
        this.clientChannel.writeAndFlush(rpcRequest).sync();
        //this.clientChannel.closeFuture().sync();
        return rpcResponse;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.rpcResponse = rpcResponse;
    }
}
