package fr.toutatice.portail.api.cache.services;

import java.io.Serializable;

public interface IServiceInvoker extends Serializable{

	public Object invoke() throws Exception;
	
	
}
