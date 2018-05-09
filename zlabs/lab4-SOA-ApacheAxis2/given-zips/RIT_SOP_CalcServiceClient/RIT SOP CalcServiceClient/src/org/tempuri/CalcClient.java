package org.tempuri;

import java.rmi.RemoteException;

public class CalcClient {

	public static void main(String[] args) {
		IServiceProxy proxy = new IServiceProxy();
		try {
			System.out.println(proxy.multiple(7.0,9.0));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
