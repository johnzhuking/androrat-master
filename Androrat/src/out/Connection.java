//package InOut;

package out;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.util.Log;

import out.Mux;

import in.Demux;
import in.Receiver;
import inout.Controler;

public class Connection {
	Socket s;
	String ip = "localhost";
	int port = 5555;
	DataOutputStream out;
	DataInputStream in;

	boolean stop = false;
	ByteBuffer readInstruction;
	Mux m;
	Demux dem;
	Controler controler;
	Receiver receive;

	public Connection(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public Connection(String ip, int port, Controler ctrl) {
		this.ip = ip;
		this.port = port;
		this.controler = ctrl;
	}

	public boolean connect() {
		try {
			s = new Socket(ip, port);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			m = new Mux(out);
			dem = new Demux(controler, "moi");
			receive = new Receiver(s);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	
	/**
	 * 链接是否正常通信
	 * */
	public boolean sendMsg() {

		try {
			s.sendUrgentData(0xFF);
		} catch (IOException e) {
			// 网络断开
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * 链接是否正常通信
	 * */
	public boolean sendMsg2() {

		if(s!=null&&!s.isClosed()&&s.isConnected()){
			Log.e("Connection", "Connection" + s.isClosed() + ";;;;s.isConnected()" + s.isConnected());
			return true;
		}
		return false;

	}
	
	public boolean isAlive(){  
        if(s.equals(null)||s.isClosed()||!s.isConnected()||s.isInputShutdown()||s.isOutputShutdown()){  
            return false;  
        }else{  
            return true;  

        }  
    } 
	

	public boolean reconnect() {
		return connect();
	}

	public boolean accept(ServerSocket ss) {
		try {
			s = ss.accept();

			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			m = new Mux(out);
			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;
		}
	}

	public ByteBuffer getInstruction() throws Exception {
		readInstruction = receive.read();

		if (dem.receive(readInstruction))
			readInstruction.compact();
		else
			readInstruction.clear();

		return readInstruction;
	}

	public void sendData(int chan, byte[] packet) {
		m.send(chan, packet);
	}

	public void stop() {
		try {
			if (s != null) {
				s.close();
			}
		} catch (IOException e) {

		}
	}
}