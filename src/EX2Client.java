import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.zip.CRC32;

public class EX2Client {
	public static void main(String[] args ){
		try (Socket socket = new Socket("18.221.102.182", 38102)) {
			System.out.println("Connected to server.");
			InputStream is = socket.getInputStream();
			
			byte[] msg = new byte[100];
			
			for( int i = 0; i < msg.length ; i++){
//				if ( i != 0 && i % 10 == 0){
//					System.out.print("\n");
//				}
				int x1 = is.read();
				int x2 = is.read();
//				System.out.print(String.format("%02X %02X,", x1, x2));
				msg[i] = (byte)(x1 * 0x10 + x2 );
			}
			
			System.out.print("Received bytes:");
			for( int i = 0; i < msg.length ; i++){
				if ( i % 10 == 0){
					System.out.print("\n   ");
				}
				System.out.print(String.format("%02X", msg[i]));
			}
			System.out.println();
			CRC32 crc = new CRC32();
			crc.update(msg, 0, msg.length);
			long crcVal = crc.getValue();
			OutputStream os = socket.getOutputStream();
			System.out.println("Generated CRC Value: " + Long.toHexString(crcVal).toUpperCase() );
			byte[] crcBytes = new byte[4];
			for ( int i = 3; i >= 0; i--){
				crcBytes[i] = (byte)(crcVal & 0xFF);
				crcVal = crcVal / 0x100;
			}
//			for ( int i = 0; i < 4; i++){
//				crcBytes[i] = (byte)(crcVal & 0xFF);
//				crcVal = crcVal / 0x100;
//			}
//			System.out.println(Arrays.toString(crcBytes));
//			for( int i = 0; i < 4 ; i++){
//				System.out.print(String.format("%02X ", crcBytes[i] ));
//			}
			os.write(crcBytes, 0, 4);
//			while ( true ) {
//				System.out.println(is.read());
//			}
			
			int result = is.read();
			if ( result ==  1 ){
				System.out.println("Response good.");
			} else {
				System.out.println("Response bad.");
			}

//			System.out.println("Disconnected from server.");
			
        } catch ( Exception e ){
        	e.printStackTrace();
        } finally {
        	System.out.println("Disconnected from server.");
        }
		
	}
}
