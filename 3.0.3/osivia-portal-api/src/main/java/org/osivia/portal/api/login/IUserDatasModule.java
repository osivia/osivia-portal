package org.osivia.portal.api.login;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface IUserDatasModule {
	
	public void computeUserDatas( HttpServletRequest request, Map<String, Object> datas);

}
