import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CRC32;

public class EX2Client {
	public static void main(String[] args ){
		try (Socket socket = new Socket("localhost", 38102)) {
			System.out.println("Connected to server.");
			InputStream is = socket.getInputStream();
			byte[] msg = new byte[100];
			byte[] temp = new byte[2];
			System.out.print("Received bytes:");
			for( int i = 0; i < 100; i++){
				is.read(temp, 0, 2);
				msg[i] = (byte)(temp[0] * 0x10 + temp[1]);
				if ( i % 10 == 0){
					System.out.print("\n   ");
				}
				System.out.print(String.format("%02X", msg[i]));
			}
			System.out.println();
			CRC32 crc = new CRC32();
			crc.update(msg);
			long crcVal = crc.getValue();
			OutputStream os = socket.getOutputStream();
			System.out.println("Generated CRC Value: " + Long.toHexString(crcVal).toUpperCase() );
			byte[] crcBytes = new byte[4];
			for ( int i = 3; i >= 0; i--){
				crcBytes[i] = (byte)(crcVal & 0xFF);
				crcVal = crcVal / 0x100;
			}
			os.write(crcBytes, 0, 4);
			if ( is.read() == 1 ){
				System.out.println("Response good.");
			} else {
				System.out.println("Response bad.");
			}
			socket.close();
			System.out.println("Disconnected from server.");
        } catch ( Exception e ){
        	e.printStackTrace();
        }
		
	}
}
