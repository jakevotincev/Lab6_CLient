import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * <p>Класс для считывания и выполнения команд</p>
 *
 * @author Вотинцев Евгений
 * @version 1.0
 */
public class CommandReader {

    private String command = "";
    /**
     * <p>Объект команды</p>
     */
    Animal current_object;
    private Command command1 = new Command();

    /**
     * <p>Считает количество заданных символов в строке</p>
     *
     * @param word Строка
     * @param s    Символ
     * @return Количество
     */
    public int charCounter(String word, char s) {
        int count = 0;
        for (char element : word.toCharArray()) {
            if (element == s) count += 1;
        }
        return count;
    }

    private int charCounter(String word, char s, char e) {
        int countS = 0;
        int countE = 0;
        for (char el : word.toCharArray()) {
            if (countE % 2 == 0) {
                if (el == s) countS += 1;
            }
            if (el == e) countE += 1;
        }
        return countS;
    }


    /**
     * <p>Считывает и запускает команды</p>
     */
    public Command readCommand() {
        String object = "";
        String line = "";
        Scanner scanner = new Scanner(System.in);
        int count;
        boolean command_end;
        while (true) {
            boolean cur_command = false;

            while (!cur_command) {
                System.out.println("Введите команду ");
                try {
                    line = scanner.nextLine().trim();
                } catch (NoSuchElementException e) {
                    System.exit(0);
                }
                if (line.equals("show") || line.startsWith("remove {") || line.startsWith("add_if_min {") || line.startsWith("remove_greater {") || line.equals("info") || line.startsWith("import {") || line.startsWith("add {") || line.equals("help") || line.equals("exit") || line.equals("save")) {
                    cur_command = true;
                    command_end = false;
                } else {
                    System.err.println("Неизвестная команда или не верный формат команды ");
                    command_end = true;
                }

                if (line.startsWith("show") || line.startsWith("info") || line.startsWith("help") || line.startsWith("save") || line.startsWith("exit")) {
                    command_end = true;
                    command = line;
                }

                while (!command_end) {
                    count = 0;
                    if (charCounter(line, '{', '\"') != charCounter(line, '}', '\"') || charCounter(line, '}', '\"') == 0 || charCounter(line, '{', '\"') == 0) {
                        line += scanner.nextLine();
                        count += charCounter(line, '{');
                        count -= charCounter(line, '}');
                    }
                    if (charCounter(line, '{', '\"') < charCounter(line, '}', '\"')) {
                        System.err.println("Ошибка в написании json строки, попробуйте заново");
                        cur_command = false;
                        line = "";
                        break;
                    } else {
                        if (count == 0) {
                            command_end = true;
                            try {
                                String a[] = line.split(" ", 2);
                                if (!(a[0].equals("remove") || a[0].equals("add_if_min") || a[0].equals("remove_greater") || a[0].equals("info") || a[0].equals("import") || a[0].equals("add"))) {
                                    throw new Exception();
                                }
                                object = a[1];
                                command = a[0];
                            } catch (Exception e) {
                                System.err.println("Неизвестная команда или не верный формат команды");
                                line = "";
                                cur_command = false;
                            }
                        }
                    }
                }

            }
            if (!command.equals("import") && !command.equals("show") && !command.equals("help") && !command.equals("info") && !command.equals("exit") && !command.equals("save")) {
                try {
                    current_object = (Animal) AnimalFactory.createAnimal(object);
                } catch (NumberFormatException e) {
                    System.err.println("Неверный формат атрибута");
                    continue;
                } catch (ClassNotFoundException e) {
                    System.err.println("Атрибут класс не найден");
                    continue;
                } catch (JsonSyntaxException e) {
                    System.err.println("Ошибка синтаксиса json");
                    continue;
                }
            }
            if (!command.equals("exit")) {
                try {
                    if (command.equals("import")) {
                        object = object.replace("{", "").replace("}", "");
                        ParserXML parser = new ParserXML();
                        if (object.equals("/dev/urandom") || object.equals("/dev/random")) {
                            System.err.println("Ошибка xml файла");
                            continue;
                        }
                        String file = parser.readXML(object);
                        command1.setName(command);
                        command1.setFile(file);
                        break;
                    } else {
                        command1.setName(command);
                        command1.setObject(current_object);
                        break;
                    }

                } catch (FileNotFoundException e) {
                    System.err.println("Файл не найден или к нему нет доступа");
                }
            } else {
                System.out.println("Изменения не сохранены");
                System.exit(0);
            }

        }
        return command1;
    }
}

