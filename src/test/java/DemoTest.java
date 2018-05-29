/**
 * Created by yang on 18-5-29.
 */
public class DemoTest {
    public static void main(String[] args) {
        String ip = "192.168.10.15";
        String ip2 = "192.168.10.3";
        String port = "24800";
        String port2 = "50184";

        System.out.println(ip.hashCode());
        System.out.println(ip2.hashCode());
        System.out.println(port.hashCode());
        System.out.println(port2.hashCode());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ip);
        System.out.println(stringBuilder.toString());
        System.out.println(stringBuilder.toString());

    }
}
