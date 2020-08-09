import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ObjectReadHandler extends ChannelInboundHandlerAdapter {

    private final String PATH = "./Server/files/";

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client disconnected");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String command = msg.toString();
        System.out.println("Command : " + command);
        String [] op = command.split(" ");
        if (op[0].equals("./download")) {
            String fileName = op[1];
            File file = new File(PATH + fileName);
            System.out.println("file name : " + fileName);
            if (file.exists()) {
                System.out.println("file exists");
                ctx.channel().writeAndFlush(Long.toString(file.length()));
                System.out.println("Send length of file : " + file.length());
                Files.copy(Paths.get(file.getAbsolutePath()), new FileOutputStream(file));
            }
        }
    }
}
