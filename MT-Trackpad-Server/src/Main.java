import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
	private static Robot r;
	private static InetAddress link;
	private static double sensitivity;
	
	public static void main(String[] args) throws IOException, AWTException {
		r = new Robot();
		ServerSocket ss = new ServerSocket(12345);
		sensitivity = 3;
		
		while (true){
			System.out.println("Server Started");
			Socket s = ss.accept();
			DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			int read = in.readInt();
			if (read==1245563)
			{
				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
				out.writeInt(1245567);
				out.flush();
				link = s.getInetAddress();
				System.out.println("This server has been discovered by the device");
			}
			in.close();
			s.close();
			s = ss.accept();
			System.out.println("Client connected");
			if (s.getInetAddress().equals(link))
			{
				in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
				while(s.isBound())
				{
					int event = (int)in.readFloat();
					switch(event)
					{
					case 0:
						click(0);
						break;
					case 1: // Mouse Move
						float x = in.readFloat();
						float y = in.readFloat();
						moveMouse(x,y);
						break;
					case 2:
						click(1);
						break;
					case 3:
						r.mousePress(InputEvent.BUTTON1_MASK);
						break;
					case 4:
						r.mouseRelease(InputEvent.BUTTON1_MASK);
						break;
					default:
						break;
					}
				}
			}
		}
	}
	
	public static void click(int lOrR)
	{
		if (lOrR==0)
		{
			r.mousePress(InputEvent.BUTTON1_MASK);
			r.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		else if (lOrR==1)
		{
			r.mousePress(InputEvent.BUTTON3_MASK);
			r.mouseRelease(InputEvent.BUTTON3_MASK);
		}
	}
	
	public static void moveMouse(float dx, float dy)
	{
		dx *= sensitivity;
		dy *= sensitivity;
		
		Point ml = MouseInfo.getPointerInfo().getLocation();
		float x = ml.x;
		float y = ml.y;
		
		float newx = x+dx;
		float newy = y+dy;
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (newx<0)
			newx = 0;
		if (newy<0)
			newy = 0;
		if (newx>d.width)
			newx = d.width-1;
		if (newy>d.height)
			newy = d.height-1;
		
		r.mouseMove((int)newx, (int)newy);
	}

}
