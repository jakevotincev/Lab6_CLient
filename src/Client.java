import java.io.*;
import java.net.*;


public class Client {


    public static void main(String[] args) {
        int port = 4999;
        boolean connected = false;
        try {
            if (args.length > 0) port = Integer.valueOf(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Не верный формат порта, введите значение int");
            System.exit(1);
        }
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.err.println("Неизвестный хост");
        }
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(1000);
        } catch (SocketException e) {
            System.err.println("Ошибка связанная с сокетами");
        }
        InetAddress finalAddress = address;
        DatagramSocket finalSocket = socket;
        int finalPort = port;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
                Command command = new Command();
                command.setName("save");
                objectOutputStream.writeObject(command);
                DatagramPacket packet = new DatagramPacket(out.toByteArray(), 0, out.toByteArray().length, finalAddress, finalPort);
                finalSocket.send(packet);
            } catch (IOException e) {
                System.err.println("Ошибка, которая не должна была возникнуть, свяжитесь с разработчиком (код ошибки: 03)");
            }
        }));
        Command command = new Command();
        command.setName("connect");
        while (true) {
            if (connected) System.out.println("Используйте команду help, чтобы получить информацию о командах");
            CommandReader reader = new CommandReader();
            if (connected) command = reader.readCommand();
            byte[] a = new byte[8192];
            for (int i = 0; i < 5; i++) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                     ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
                    objectOutputStream.writeObject(command);
                    DatagramPacket packet = new DatagramPacket(out.toByteArray(), 0, out.toByteArray().length, address, port);
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DatagramPacket packet1 = new DatagramPacket(a, a.length);
                try {
                    socket.receive(packet1);
                    break;
                } catch (SocketTimeoutException e) {
                    if (i == 0) System.out.println("Сервер не отвечает");
                    System.out.println("Соединение с сервером...");
                } catch (IOException e) {
                    System.err.println("Ошибка, которая не должна была возникнуть, свяжитесь с разработчиком (код ошибки: 02)");
                }
            }
            try (ByteArrayInputStream in = new ByteArrayInputStream(a);
                 ObjectInputStream objectInputStream = new ObjectInputStream(in)) {
                System.out.println(objectInputStream.readObject());
                connected = true;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Сервер не доступен");
                System.exit(1);
            }
        }

    }

}
