import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public ListView<String> lv;
    public TextField txt;
    public Button send;
    private Socket socket;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private final String clientFilesPath = "./Client/files/";
    private final int bufferSize = 1024;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost", 8189);
            is = new ObjectDecoderInputStream(socket.getInputStream());
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File dir = new File(clientFilesPath);
        for (String file : dir.list()) {
            lv.getItems().add(file);
        }
    }

    // ./download fileName
    // ./upload fileName
    public void sendCommand(ActionEvent actionEvent) {
        String command = txt.getText();
        String [] op = command.split(" ");
        if (op[0].equals("./download")) {
            try {
                os.writeUTF(command);
                byte[] buffer = new byte[bufferSize];
                System.out.println("Command : " + command);
                String buferString = is.readUTF();
                long len = Long.parseLong(buferString);
                System.out.println("Length of file : " + len);
                File file = new File(clientFilesPath + "/" + op[1]);
                if (!file.exists()) {
                    file.createNewFile();
                }
                System.out.println("File size is: " + len);

                try(FileOutputStream fos = new FileOutputStream(file)) {
                    if (len < bufferSize) {
                        int count = is.read(buffer);
                        fos.write(buffer, 0, count);
                    } else {
                        for (long i = 0; i < len / bufferSize; i++) {
                            int count = is.read(buffer);
                            fos.write(buffer, 0, count);
                        }
                    }
                }
                lv.getItems().add(op[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}
