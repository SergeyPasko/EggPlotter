import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 02.12.17
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class TestCOM {
    private static SerialPort serialPort;

    public static void main(String[] args) throws InterruptedException {
        //Передаём в конструктор имя порта
        serialPort = new SerialPort("COM1");
        try {
            //Открываем порт
            serialPort.openPort();
            //Выставляем параметры
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            //Включаем аппаратное управление потоком
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            System.out.println("false");
            serialPort.setDTR(false);
            Thread.sleep(5000);
            System.out.println("true");
            serialPort.setDTR(true);
            Thread.sleep(5000);

        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

}
