package fr.toutatice.portail.api.login;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface IUserDatasModule {
	
	public void computeUserDatas( HttpServletRequest request, Map<String, Object> datas);

}
