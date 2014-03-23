/* PIA : 
 * 
 * synchronisation des attributs
 * renommage du JSESSIONID en PORTALSESSIONID
 */


/*      */ package org.apache.catalina.connector;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.security.Principal;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import java.util.TreeMap;
/*      */ import javax.security.auth.Subject;
/*      */ import javax.servlet.FilterChain;
/*      */ import javax.servlet.RequestDispatcher;
/*      */ import javax.servlet.ServletContext;
/*      */ import javax.servlet.ServletInputStream;
/*      */ import javax.servlet.ServletRequestAttributeEvent;
/*      */ import javax.servlet.ServletRequestAttributeListener;
/*      */ import javax.servlet.http.Cookie;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.servlet.http.HttpServletResponse;
/*      */ import javax.servlet.http.HttpSession;
/*      */ import org.apache.catalina.Context;
/*      */ import org.apache.catalina.Globals;
/*      */ import org.apache.catalina.Host;
/*      */ import org.apache.catalina.Manager;
/*      */ import org.apache.catalina.Realm;
/*      */ import org.apache.catalina.Session;
/*      */ import org.apache.catalina.Wrapper;
/*      */ import org.apache.catalina.core.ApplicationFilterFactory;
/*      */ import org.apache.catalina.realm.GenericPrincipal;
/*      */ import org.apache.catalina.util.Enumerator;
/*      */ import org.apache.catalina.util.ParameterMap;
/*      */ import org.apache.catalina.util.RequestUtil;
/*      */ import org.apache.catalina.util.StringManager;
/*      */ import org.apache.catalina.util.StringParser;
/*      */ import org.apache.coyote.ActionCode;
/*      */ import org.apache.tomcat.util.buf.B2CConverter;
/*      */ import org.apache.tomcat.util.buf.MessageBytes;

// AJOUT PIA
/*      */ //import org.apache.tomcat.util.buf.StringCache.ByteEntry;
/*      */ //import org.apache.tomcat.util.buf.StringCache.CharEntry;

/*      */ import org.apache.tomcat.util.buf.StringCache;

/*      */ import org.apache.tomcat.util.http.Cookies;
/*      */ import org.apache.tomcat.util.http.FastHttpDateFormat;
/*      */ import org.apache.tomcat.util.http.MimeHeaders;
/*      */ import org.apache.tomcat.util.http.Parameters;
/*      */ import org.apache.tomcat.util.http.ServerCookie;
/*      */ import org.apache.tomcat.util.http.mapper.MappingData;
import org.jboss.logging.Logger;
/*      */ 
/*      */ public class Request
/*      */   implements HttpServletRequest
/*      */ {
/*      */   protected org.apache.coyote.Request coyoteRequest;
/*      */   protected static final TimeZone GMT_ZONE;
/*      */   protected static StringManager sm;
/*  147 */   protected Cookie[] cookies = null;
/*      */ 
/*  156 */   protected SimpleDateFormat[] formats = { new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US) };
/*      */   protected static Locale defaultLocale;
/*  172 */   protected HashMap attributes = new HashMap();
/*      */ 
/*  178 */   private HashMap readOnlyAttributes = new HashMap();
/*      */ 
/*  184 */   protected ArrayList locales = new ArrayList();
/*      */ 
/*  191 */   private transient HashMap notes = new HashMap();
/*      */ 
/*  197 */   protected String authType = null;
/*      */ 
/*  203 */   protected CometEventImpl event = null;
/*      */ 
/*  209 */   protected boolean comet = false;
/*      */ 
/*  215 */   protected Object dispatcherType = null;
/*      */ 
/*  221 */   protected InputBuffer inputBuffer = new InputBuffer();
/*      */ 
/*  227 */   protected CoyoteInputStream inputStream = new CoyoteInputStream(this.inputBuffer);
/*      */ 
/*  234 */   protected CoyoteReader reader = new CoyoteReader(this.inputBuffer);
/*      */ 
/*  240 */   protected boolean usingInputStream = false;
/*      */ 
/*  246 */   protected boolean usingReader = false;
/*      */ 
/*  252 */   protected Principal userPrincipal = null;
/*      */ 
/*  258 */   protected boolean sessionParsed = false;
/*      */ 
/*  264 */   protected boolean parametersParsed = false;
/*      */ 
/*  270 */   protected boolean cookiesParsed = false;
/*      */ 
/*  276 */   protected boolean secure = false;
/*      */ 
/*  282 */   protected transient Subject subject = null;
/*      */   protected static int CACHED_POST_LEN;
/*  289 */   protected byte[] postData = null;
/*      */ 
/*  295 */   protected ParameterMap parameterMap = new ParameterMap();
/*      */ 
/*  301 */   protected Session session = null;
/*      */ 
/*  307 */   protected Object requestDispatcherPath = null;
/*      */ 
/*  313 */   protected boolean requestedSessionCookie = false;
/*      */ 
/*  319 */   protected String requestedSessionId = null;
/*      */ 
/*  325 */   protected boolean requestedSessionURL = false;
/*      */ 
/*  331 */   protected boolean localesParsed = false;
/*      */ 
/*  337 */   private StringParser parser = new StringParser();
/*      */ 
/*  343 */   protected int localPort = -1;
/*      */ 
/*  348 */   protected String remoteAddr = null;
/*      */ 
/*  354 */   protected String remoteHost = null;
/*      */ 
/*  360 */   protected int remotePort = -1;
/*      */ 
/*  365 */   protected String localAddr = null;
/*      */ 
/*  371 */   protected String localName = null;
/*      */   protected Connector connector;
/*  498 */   protected Context context = null;
/*      */ 
/*  524 */   protected FilterChain filterChain = null;
/*      */   protected static final String info = "org.apache.coyote.catalina.CoyoteRequest/1.0";
/*  585 */   protected MappingData mappingData = new MappingData();
/*      */ 
/*  598 */   protected RequestFacade facade = null;
/*      */ 
/*  615 */   protected Response response = null;
/*      */ 
/*  656 */   protected B2CConverter URIConverter = null;
/*      */ 
/*  678 */   protected Wrapper wrapper = null;
/*      */ 
/*      */   public Request()
/*      */   {
/*   98 */     this.formats[0].setTimeZone(GMT_ZONE);
/*   99 */     this.formats[1].setTimeZone(GMT_ZONE);
/*  100 */     this.formats[2].setTimeZone(GMT_ZONE);
/*      */   }
/*      */ 
/*      */   public void setCoyoteRequest(org.apache.coyote.Request coyoteRequest)
/*      */   {
/*  119 */     this.coyoteRequest = coyoteRequest;
/*  120 */     this.inputBuffer.setRequest(coyoteRequest);
/*      */   }
/*      */ 
/*      */   public org.apache.coyote.Request getCoyoteRequest()
/*      */   {
/*  127 */     return this.coyoteRequest;
/*      */   }
/*      */ 
/*      */   public void recycle()
/*      */   {
/*  383 */     this.context = null;
/*  384 */     this.wrapper = null;
/*      */ 
/*  386 */     this.dispatcherType = null;
/*  387 */     this.requestDispatcherPath = null;
/*      */ 
/*  389 */     this.comet = false;
/*  390 */     if (this.event != null) {
/*  391 */       this.event.clear();
/*  392 */       this.event = null;
/*      */     }
/*      */ 
/*  395 */     this.authType = null;
/*  396 */     this.inputBuffer.recycle();
/*  397 */     this.usingInputStream = false;
/*  398 */     this.usingReader = false;
/*  399 */     this.userPrincipal = null;
/*  400 */     this.subject = null;
/*  401 */     this.sessionParsed = false;
/*  402 */     this.parametersParsed = false;
/*  403 */     this.cookiesParsed = false;
/*  404 */     this.locales.clear();
/*  405 */     this.localesParsed = false;
/*  406 */     this.secure = false;
/*  407 */     this.remoteAddr = null;
/*  408 */     this.remoteHost = null;
/*  409 */     this.remotePort = -1;
/*  410 */     this.localPort = -1;
/*  411 */     this.localAddr = null;
/*  412 */     this.localName = null;
/*      */ 
/*  414 */     this.attributes.clear();
/*  415 */     this.notes.clear();
/*  416 */     this.cookies = null;
/*      */ 
/*  418 */     if (this.session != null) {
/*  419 */       this.session.endAccess();
/*      */     }
/*  421 */     this.session = null;
/*  422 */     this.requestedSessionCookie = false;
/*  423 */     this.requestedSessionId = null;
/*  424 */     this.requestedSessionURL = false;
/*      */ 
/*  426 */     if ((Globals.IS_SECURITY_ENABLED) || (Connector.RECYCLE_FACADES)) {
/*  427 */       this.parameterMap = new ParameterMap();
/*      */     } else {
/*  429 */       this.parameterMap.setLocked(false);
/*  430 */       this.parameterMap.clear();
/*      */     }
/*      */ 
/*  433 */     this.mappingData.recycle();
/*      */ 
/*  435 */     if ((Globals.IS_SECURITY_ENABLED) || (Connector.RECYCLE_FACADES)) {
/*  436 */       if (this.facade != null) {
/*  437 */         this.facade.clear();
/*  438 */         this.facade = null;
/*      */       }
/*  440 */       if (this.inputStream != null) {
/*  441 */         this.inputStream.clear();
/*  442 */         this.inputStream = null;
/*      */       }
/*  444 */       if (this.reader != null) {
/*  445 */         this.reader.clear();
/*  446 */         this.reader = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearEncoders()
/*      */   {
/*  457 */     this.inputBuffer.clearEncoders();
/*      */   }
/*      */ 
/*      */   public boolean read()
/*      */     throws IOException
/*      */   {
/*  466 */     return this.inputBuffer.realReadBytes(null, 0, 0) > 0;
/*      */   }
/*      */ 
/*      */   public Connector getConnector()
/*      */   {
/*  482 */     return this.connector;
/*      */   }
/*      */ 
/*      */   public void setConnector(Connector connector)
/*      */   {
/*  491 */     this.connector = connector;
/*      */   }
/*      */ 
/*      */   public Context getContext()
/*      */   {
/*  504 */     return this.context;
/*      */   }
/*      */ 
/*      */   public void setContext(Context context)
/*      */   {
/*  517 */     this.context = context;
/*      */   }
/*      */ 
/*      */   public FilterChain getFilterChain()
/*      */   {
/*  530 */     return this.filterChain;
/*      */   }
/*      */ 
/*      */   public void setFilterChain(FilterChain filterChain)
/*      */   {
/*  539 */     this.filterChain = filterChain;
/*      */   }
/*      */ 
/*      */   public Host getHost()
/*      */   {
/*  547 */     if (getContext() == null)
/*  548 */       return null;
/*  549 */     return (Host)getContext().getParent();
/*      */   }
/*      */ 
/*      */   public void setHost(Host host)
/*      */   {
/*  562 */     this.mappingData.host = host;
/*      */   }
/*      */ 
/*      */   public String getInfo()
/*      */   {
/*  578 */     return "org.apache.coyote.catalina.CoyoteRequest/1.0";
/*      */   }
/*      */ 
/*      */   public MappingData getMappingData()
/*      */   {
/*  591 */     return this.mappingData;
/*      */   }
/*      */ 
/*      */   public HttpServletRequest getRequest()
/*      */   {
/*  605 */     if (this.facade == null) {
/*  606 */       this.facade = new RequestFacade(this);
/*      */     }
/*  608 */     return this.facade;
/*      */   }
/*      */ 
/*      */   public Response getResponse()
/*      */   {
/*  621 */     return this.response;
/*      */   }
/*      */ 
/*      */   public void setResponse(Response response)
/*      */   {
/*  630 */     this.response = response;
/*      */   }
/*      */ 
/*      */   public InputStream getStream()
/*      */   {
/*  637 */     if (this.inputStream == null) {
/*  638 */       this.inputStream = new CoyoteInputStream(this.inputBuffer);
/*      */     }
/*  640 */     return this.inputStream;
/*      */   }
/*      */ 
/*      */   public void setStream(InputStream stream)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected B2CConverter getURIConverter()
/*      */   {
/*  662 */     return this.URIConverter;
/*      */   }
/*      */ 
/*      */   protected void setURIConverter(B2CConverter URIConverter)
/*      */   {
/*  671 */     this.URIConverter = URIConverter;
/*      */   }
/*      */ 
/*      */   public Wrapper getWrapper()
/*      */   {
/*  684 */     return this.wrapper;
/*      */   }
/*      */ 
/*      */   public void setWrapper(Wrapper wrapper)
/*      */   {
/*  695 */     this.wrapper = wrapper;
/*      */   }
/*      */ 
/*      */   public ServletInputStream createInputStream()
/*      */     throws IOException
/*      */   {
/*  710 */     if (this.inputStream == null) {
/*  711 */       this.inputStream = new CoyoteInputStream(this.inputBuffer);
/*      */     }
/*  713 */     return this.inputStream;
/*      */   }
/*      */ 
/*      */   public void finishRequest()
/*      */     throws IOException
/*      */   {
/*      */   }
/*      */ 
/*      */   public Object getNote(String name)
/*      */   {
/*  735 */     return this.notes.get(name);
/*      */   }
/*      */ 
/*      */   public Iterator getNoteNames()
/*      */   {
/*  744 */     return this.notes.keySet().iterator();
/*      */   }
/*      */ 
/*      */   public void removeNote(String name)
/*      */   {
/*  755 */     this.notes.remove(name);
/*      */   }
/*      */ 
/*      */   public void setNote(String name, Object value)
/*      */   {
/*  767 */     this.notes.put(name, value);
/*      */   }
/*      */ 
/*      */   public void setContentLength(int length)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setContentType(String type)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setProtocol(String protocol)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setRemoteAddr(String remoteAddr)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setRemoteHost(String remoteHost)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setScheme(String scheme)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setSecure(boolean secure)
/*      */   {
/*  842 */     this.secure = secure;
/*      */   }
/*      */ 
/*      */   public void setServerName(String name)
/*      */   {
/*  852 */     this.coyoteRequest.serverName().setString(name);
/*      */   }
/*      */ 
/*      */   public void setServerPort(int port)
/*      */   {
/*  862 */     this.coyoteRequest.setServerPort(port);
/*      */   }
/*      */ 
/*      */   public synchronized Object getAttribute(String name)
/*      */   {
/*  877 */     if (name.equals("org.apache.catalina.core.DISPATCHER_TYPE")) {
/*  878 */       return this.dispatcherType == null ? ApplicationFilterFactory.REQUEST_INTEGER : this.dispatcherType;
/*      */     }
/*      */ 
/*  881 */     if (name.equals("org.apache.catalina.core.DISPATCHER_REQUEST_PATH")) {
/*  882 */       return this.requestDispatcherPath == null ? getRequestPathMB().toString() : this.requestDispatcherPath.toString();
/*      */     }
/*      */ 
/*  887 */     Object attr = this.attributes.get(name);
/*      */ 
/*  889 */     if (attr != null) {
/*  890 */       return attr;
/*      */     }
/*  892 */     attr = this.coyoteRequest.getAttribute(name);
/*  893 */     if (attr != null)
/*  894 */       return attr;
/*  895 */     if (isSSLAttribute(name)) {
/*  896 */       this.coyoteRequest.action(ActionCode.ACTION_REQ_SSL_ATTRIBUTE, this.coyoteRequest);
/*      */ 
/*  898 */       attr = this.coyoteRequest.getAttribute("javax.servlet.request.X509Certificate");
/*  899 */       if (attr != null) {
/*  900 */         this.attributes.put("javax.servlet.request.X509Certificate", attr);
/*      */       }
/*  902 */       attr = this.coyoteRequest.getAttribute("javax.servlet.request.cipher_suite");
/*  903 */       if (attr != null) {
/*  904 */         this.attributes.put("javax.servlet.request.cipher_suite", attr);
/*      */       }
/*  906 */       attr = this.coyoteRequest.getAttribute("javax.servlet.request.key_size");
/*  907 */       if (attr != null) {
/*  908 */         this.attributes.put("javax.servlet.request.key_size", attr);
/*      */       }
/*  910 */       attr = this.coyoteRequest.getAttribute("javax.servlet.request.ssl_session");
/*  911 */       if (attr != null) {
/*  912 */         this.attributes.put("javax.servlet.request.ssl_session", attr);
/*      */       }
/*  914 */       attr = this.attributes.get(name);
/*      */     }
/*  916 */     return attr;
/*      */   }
/*      */ 
/*      */   static boolean isSSLAttribute(String name)
/*      */   {
/*  924 */     return ("javax.servlet.request.X509Certificate".equals(name)) || ("javax.servlet.request.cipher_suite".equals(name)) || ("javax.servlet.request.key_size".equals(name)) || ("javax.servlet.request.ssl_session".equals(name));
/*      */   }
/*      */ 
/*      */   public synchronized Enumeration getAttributeNames()
/*      */   {
/*  935 */     if (isSecure()) {
/*  936 */       getAttribute("javax.servlet.request.X509Certificate");
/*      */     }
/*  938 */     return new Enumerator(this.attributes.keySet(), true);
/*      */   }
/*      */ 
/*      */   public String getCharacterEncoding()
/*      */   {
/*  946 */     return this.coyoteRequest.getCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public int getContentLength()
/*      */   {
/*  954 */     return this.coyoteRequest.getContentLength();
/*      */   }
/*      */ 
/*      */   public String getContentType()
/*      */   {
/*  962 */     return this.coyoteRequest.getContentType();
/*      */   }
/*      */ 
/*      */   public ServletInputStream getInputStream()
/*      */     throws IOException
/*      */   {
/*  977 */     if (this.usingReader) {
/*  978 */       throw new IllegalStateException(sm.getString("coyoteRequest.getInputStream.ise"));
/*      */     }
/*      */ 
/*  981 */     this.usingInputStream = true;
/*  982 */     if (this.inputStream == null) {
/*  983 */       this.inputStream = new CoyoteInputStream(this.inputBuffer);
/*      */     }
/*  985 */     return this.inputStream;
/*      */   }
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  998 */     if (!this.localesParsed) {
/*  999 */       parseLocales();
/*      */     }
/* 1001 */     if (this.locales.size() > 0) {
/* 1002 */       return (Locale)this.locales.get(0);
/*      */     }
/* 1004 */     return defaultLocale;
/*      */   }
/*      */ 
/*      */   public Enumeration getLocales()
/*      */   {
/* 1018 */     if (!this.localesParsed) {
/* 1019 */       parseLocales();
/*      */     }
/* 1021 */     if (this.locales.size() > 0)
/* 1022 */       return new Enumerator(this.locales);
/* 1023 */     ArrayList results = new ArrayList();
/* 1024 */     results.add(defaultLocale);
/* 1025 */     return new Enumerator(results);
/*      */   }
/*      */ 
/*      */   public String getParameter(String name)
/*      */   {
/* 1039 */     if (!this.parametersParsed) {
/* 1040 */       parseParameters();
/*      */     }
/* 1042 */     return this.coyoteRequest.getParameters().getParameter(name);
/*      */   }
/*      */ 
/*      */   public Map getParameterMap()
/*      */   {
/* 1059 */     if (this.parameterMap.isLocked()) {
/* 1060 */       return this.parameterMap;
/*      */     }
/* 1062 */     Enumeration enumeration = getParameterNames();
/* 1063 */     while (enumeration.hasMoreElements()) {
/* 1064 */       String name = enumeration.nextElement().toString();
/* 1065 */       String[] values = getParameterValues(name);
/* 1066 */       this.parameterMap.put(name, values);
/*      */     }
/*      */ 
/* 1069 */     this.parameterMap.setLocked(true);
/*      */ 
/* 1071 */     return this.parameterMap;
/*      */   }
/*      */ 
/*      */   public Enumeration getParameterNames()
/*      */   {
/* 1081 */     if (!this.parametersParsed) {
/* 1082 */       parseParameters();
/*      */     }
/* 1084 */     return this.coyoteRequest.getParameters().getParameterNames();
/*      */   }
/*      */ 
/*      */   public String[] getParameterValues(String name)
/*      */   {
/* 1097 */     if (!this.parametersParsed) {
/* 1098 */       parseParameters();
/*      */     }
/* 1100 */     return this.coyoteRequest.getParameters().getParameterValues(name);
/*      */   }
/*      */ 
/*      */   public String getProtocol()
/*      */   {
/* 1109 */     return this.coyoteRequest.protocol().toString();
/*      */   }
/*      */ 
/*      */   public BufferedReader getReader()
/*      */     throws IOException
/*      */   {
/* 1124 */     if (this.usingInputStream) {
/* 1125 */       throw new IllegalStateException(sm.getString("coyoteRequest.getReader.ise"));
/*      */     }
/*      */ 
/* 1128 */     this.usingReader = true;
/* 1129 */     this.inputBuffer.checkConverter();
/* 1130 */     if (this.reader == null) {
/* 1131 */       this.reader = new CoyoteReader(this.inputBuffer);
/*      */     }
/* 1133 */     return this.reader;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public String getRealPath(String path)
/*      */   {
/* 1148 */     if (this.context == null)
/* 1149 */       return null;
/* 1150 */     ServletContext servletContext = this.context.getServletContext();
/* 1151 */     if (servletContext == null)
/* 1152 */       return null;
/*      */     try
/*      */     {
/* 1155 */       return servletContext.getRealPath(path); } catch (IllegalArgumentException e) {
/*      */     }
/* 1157 */     return null;
/*      */   }
/*      */ 
/*      */   public String getRemoteAddr()
/*      */   {
/* 1168 */     if (this.remoteAddr == null) {
/* 1169 */       this.coyoteRequest.action(ActionCode.ACTION_REQ_HOST_ADDR_ATTRIBUTE, this.coyoteRequest);
/*      */ 
/* 1171 */       this.remoteAddr = this.coyoteRequest.remoteAddr().toString();
/*      */     }
/* 1173 */     return this.remoteAddr;
/*      */   }
/*      */ 
/*      */   public String getRemoteHost()
/*      */   {
/* 1181 */     if (this.remoteHost == null) {
/* 1182 */       if (!this.connector.getEnableLookups()) {
/* 1183 */         this.remoteHost = getRemoteAddr();
/*      */       } else {
/* 1185 */         this.coyoteRequest.action(ActionCode.ACTION_REQ_HOST_ATTRIBUTE, this.coyoteRequest);
/*      */ 
/* 1187 */         this.remoteHost = this.coyoteRequest.remoteHost().toString();
/*      */       }
/*      */     }
/* 1190 */     return this.remoteHost;
/*      */   }
/*      */ 
/*      */   public int getRemotePort()
/*      */   {
/* 1198 */     if (this.remotePort == -1) {
/* 1199 */       this.coyoteRequest.action(ActionCode.ACTION_REQ_REMOTEPORT_ATTRIBUTE, this.coyoteRequest);
/*      */ 
/* 1201 */       this.remotePort = this.coyoteRequest.getRemotePort();
/*      */     }
/* 1203 */     return this.remotePort;
/*      */   }
/*      */ 
/*      */   public String getLocalName()
/*      */   {
/* 1211 */     if (this.localName == null) {
/* 1212 */       this.coyoteRequest.action(ActionCode.ACTION_REQ_LOCAL_NAME_ATTRIBUTE, this.coyoteRequest);
/*      */ 
/* 1214 */       this.localName = this.coyoteRequest.localName().toString();
/*      */     }
/* 1216 */     return this.localName;
/*      */   }
/*      */ 
/*      */   public String getLocalAddr()
/*      */   {
/* 1224 */     if (this.localAddr == null) {
/* 1225 */       this.coyoteRequest.action(ActionCode.ACTION_REQ_LOCAL_ADDR_ATTRIBUTE, this.coyoteRequest);
/*      */ 
/* 1227 */       this.localAddr = this.coyoteRequest.localAddr().toString();
/*      */     }
/* 1229 */     return this.localAddr;
/*      */   }
/*      */ 
/*      */   public int getLocalPort()
/*      */   {
/* 1238 */     if (this.localPort == -1) {
/* 1239 */       this.coyoteRequest.action(ActionCode.ACTION_REQ_LOCALPORT_ATTRIBUTE, this.coyoteRequest);
/*      */ 
/* 1241 */       this.localPort = this.coyoteRequest.getLocalPort();
/*      */     }
/* 1243 */     return this.localPort;
/*      */   }
/*      */ 
/*      */   public RequestDispatcher getRequestDispatcher(String path)
/*      */   {
/* 1254 */     if (this.context == null) {
/* 1255 */       return null;
/*      */     }
/*      */ 
/* 1258 */     if (path == null)
/* 1259 */       return null;
/* 1260 */     if (path.startsWith("/")) {
/* 1261 */       return this.context.getServletContext().getRequestDispatcher(path);
/*      */     }
/*      */ 
/* 1264 */     String servletPath = (String)getAttribute("javax.servlet.include.servlet_path");
/* 1265 */     if (servletPath == null) {
/* 1266 */       servletPath = getServletPath();
/*      */     }
/*      */ 
/* 1269 */     String pathInfo = getPathInfo();
/* 1270 */     String requestPath = null;
/*      */ 
/* 1272 */     if (pathInfo == null)
/* 1273 */       requestPath = servletPath;
/*      */     else {
/* 1275 */       requestPath = servletPath + pathInfo;
/*      */     }
/*      */ 
/* 1278 */     int pos = requestPath.lastIndexOf('/');
/* 1279 */     String relative = null;
/* 1280 */     if (pos >= 0) {
/* 1281 */       relative = RequestUtil.normalize(requestPath.substring(0, pos + 1) + path);
/*      */     }
/*      */     else {
/* 1284 */       relative = RequestUtil.normalize(requestPath + path);
/*      */     }
/*      */ 
/* 1287 */     return this.context.getServletContext().getRequestDispatcher(relative);
/*      */   }
/*      */ 
/*      */   public String getScheme()
/*      */   {
/* 1296 */     return this.coyoteRequest.scheme().toString();
/*      */   }
/*      */ 
/*      */   public String getServerName()
/*      */   {
/* 1304 */     return this.coyoteRequest.serverName().toString();
/*      */   }
/*      */ 
/*      */   public int getServerPort()
/*      */   {
/* 1312 */     return this.coyoteRequest.getServerPort();
/*      */   }
/*      */ 
/*      */   public boolean isSecure()
/*      */   {
/* 1320 */     return this.secure;
/*      */   }
/*      */ 
/*      */   public synchronized void removeAttribute(String name)
/*      */   {
/* 1330 */     Object value = null;
/* 1331 */     boolean found = false;
/*      */ 
/* 1336 */     if (this.readOnlyAttributes.containsKey(name)) {
/* 1337 */       return;
/*      */     }
/*      */ 
/* 1341 */     if (name.startsWith("org.apache.tomcat.")) {
/* 1342 */       this.coyoteRequest.getAttributes().remove(name);
/*      */     }
/*      */ 
/* 1345 */     found = this.attributes.containsKey(name);
/* 1346 */     if (found) {
/* 1347 */       value = this.attributes.get(name);
/* 1348 */       this.attributes.remove(name);
/*      */     } else {
/* 1350 */       return;
/*      */     }
/*      */ 
/* 1354 */     Object[] listeners = this.context.getApplicationEventListeners();
/* 1355 */     if ((listeners == null) || (listeners.length == 0))
/* 1356 */       return;
/* 1357 */     ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(this.context.getServletContext(), getRequest(), name, value);
/*      */ 
/* 1360 */     for (int i = 0; i < listeners.length; i++) {
/* 1361 */       if (!(listeners[i] instanceof ServletRequestAttributeListener))
/*      */         continue;
/* 1363 */       ServletRequestAttributeListener listener = (ServletRequestAttributeListener)listeners[i];
/*      */       try
/*      */       {
/* 1366 */         listener.attributeRemoved(event);
/*      */       } catch (Throwable t) {
/* 1368 */         this.context.getLogger().error(sm.getString("coyoteRequest.attributeEvent"), t);
/*      */ 
/* 1370 */         this.attributes.put("javax.servlet.error.exception", t);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setAttribute(String name, Object value)
/*      */   {
/* 1385 */     if (name == null) {
/* 1386 */       throw new IllegalArgumentException(sm.getString("coyoteRequest.setAttribute.namenull"));
/*      */     }
/*      */ 
/* 1390 */     if (value == null) {
/* 1391 */       removeAttribute(name);
/* 1392 */       return;
/*      */     }
/*      */ 
/* 1395 */     if (name.equals("org.apache.catalina.core.DISPATCHER_TYPE")) {
/* 1396 */       this.dispatcherType = value;
/* 1397 */       return;
/* 1398 */     }if (name.equals("org.apache.catalina.core.DISPATCHER_REQUEST_PATH")) {
/* 1399 */       this.requestDispatcherPath = value;
/* 1400 */       return;
/*      */     }
/*      */ 
/* 1403 */     Object oldValue = null;
/* 1404 */     boolean replaced = false;
/*      */ 
/* 1409 */     if (this.readOnlyAttributes.containsKey(name)) {
/* 1410 */       return;
/*      */     }
/*      */ 
/* 1413 */     oldValue = this.attributes.put(name, value);
/* 1414 */     if (oldValue != null) {
/* 1415 */       replaced = true;
/*      */     }
/*      */ 
/* 1419 */     if (name.startsWith("org.apache.tomcat.")) {
/* 1420 */       this.coyoteRequest.setAttribute(name, value);
/*      */     }
/*      */ 
/* 1424 */     Object[] listeners = this.context.getApplicationEventListeners();
/* 1425 */     if ((listeners == null) || (listeners.length == 0))
/* 1426 */       return;
/* 1427 */     ServletRequestAttributeEvent event = null;
/* 1428 */     if (replaced) {
/* 1429 */       event = new ServletRequestAttributeEvent(this.context.getServletContext(), getRequest(), name, oldValue);
/*      */     }
/*      */     else
/*      */     {
/* 1433 */       event = new ServletRequestAttributeEvent(this.context.getServletContext(), getRequest(), name, value);
/*      */     }
/*      */ 
/* 1437 */     for (int i = 0; i < listeners.length; i++) {
/* 1438 */       if (!(listeners[i] instanceof ServletRequestAttributeListener))
/*      */         continue;
/* 1440 */       ServletRequestAttributeListener listener = (ServletRequestAttributeListener)listeners[i];
/*      */       try
/*      */       {
/* 1443 */         if (replaced)
/* 1444 */           listener.attributeReplaced(event);
/*      */         else
/* 1446 */           listener.attributeAdded(event);
/*      */       }
/*      */       catch (Throwable t) {
/* 1449 */         this.context.getLogger().error(sm.getString("coyoteRequest.attributeEvent"), t);
/*      */ 
/* 1451 */         this.attributes.put("javax.servlet.error.exception", t);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterEncoding(String enc)
/*      */     throws UnsupportedEncodingException
/*      */   {
/* 1472 */     if (this.usingReader) {
/* 1473 */       return;
/*      */     }
/*      */ 
/* 1476 */     byte[] buffer = new byte[1];
/* 1477 */     buffer[0] = 97;
/* 1478 */     String dummy = new String(buffer, enc);
/*      */ 
/* 1481 */     this.coyoteRequest.setCharacterEncoding(enc);
/*      */   }
/*      */ 
/*      */   public void addCookie(Cookie cookie)
/*      */   {
/* 1496 */     if (!this.cookiesParsed) {
/* 1497 */       parseCookies();
/*      */     }
/* 1499 */     int size = 0;
/* 1500 */     if (this.cookies != null) {
/* 1501 */       size = this.cookies.length;
/*      */     }
/*      */ 
/* 1504 */     Cookie[] newCookies = new Cookie[size + 1];
/* 1505 */     for (int i = 0; i < size; i++) {
/* 1506 */       newCookies[i] = this.cookies[i];
/*      */     }
/* 1508 */     newCookies[size] = cookie;
/*      */ 
/* 1510 */     this.cookies = newCookies;
/*      */   }
/*      */ 
/*      */   public void addHeader(String name, String value)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void addLocale(Locale locale)
/*      */   {
/* 1533 */     this.locales.add(locale);
/*      */   }
/*      */ 
/*      */   public void addParameter(String name, String[] values)
/*      */   {
/* 1546 */     this.coyoteRequest.getParameters().addParameterValues(name, values);
/*      */   }
/*      */ 
/*      */   public void clearCookies()
/*      */   {
/* 1554 */     this.cookiesParsed = true;
/* 1555 */     this.cookies = null;
/*      */   }
/*      */ 
/*      */   public void clearHeaders()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void clearLocales()
/*      */   {
/* 1571 */     this.locales.clear();
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setAuthType(String type)
/*      */   {
/* 1591 */     this.authType = type;
/*      */   }
/*      */ 
/*      */   public void setContextPath(String path)
/*      */   {
/* 1604 */     if (path == null)
/* 1605 */       this.mappingData.contextPath.setString("");
/*      */     else
/* 1607 */       this.mappingData.contextPath.setString(path);
/*      */   }
/*      */ 
/*      */   public void setMethod(String method)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setQueryString(String query)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setPathInfo(String path)
/*      */   {
/* 1642 */     this.mappingData.pathInfo.setString(path);
/*      */   }
/*      */ 
/*      */   public void setRequestedSessionCookie(boolean flag)
/*      */   {
/* 1655 */     this.requestedSessionCookie = flag;
/*      */   }
/*      */ 
/*      */   public void setRequestedSessionId(String id)
/*      */   {
/* 1668 */     this.requestedSessionId = id;
/*      */   }
/*      */ 
/*      */   public void setRequestedSessionURL(boolean flag)
/*      */   {
/* 1682 */     this.requestedSessionURL = flag;
/*      */   }
/*      */ 
/*      */   public void setRequestURI(String uri)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setDecodedRequestURI(String uri)
/*      */   {
/*      */   }
/*      */ 
/*      */   public String getDecodedRequestURI()
/*      */   {
/* 1714 */     return this.coyoteRequest.decodedURI().toString();
/*      */   }
/*      */ 
/*      */   public MessageBytes getDecodedRequestURIMB()
/*      */   {
/* 1724 */     return this.coyoteRequest.decodedURI();
/*      */   }
/*      */ 
/*      */   public void setServletPath(String path)
/*      */   {
/* 1736 */     if (path != null)
/* 1737 */       this.mappingData.wrapperPath.setString(path);
/*      */   }
/*      */ 
/*      */   public void setUserPrincipal(Principal principal)
/*      */   {
/* 1750 */     if (Globals.IS_SECURITY_ENABLED) {
/* 1751 */       HttpSession session = getSession(false);
/* 1752 */       if ((this.subject != null) && (!this.subject.getPrincipals().contains(principal)))
/*      */       {
/* 1754 */         this.subject.getPrincipals().add(principal);
/* 1755 */       } else if ((session != null) && (session.getAttribute("javax.security.auth.subject") == null))
/*      */       {
/* 1757 */         this.subject = new Subject();
/* 1758 */         this.subject.getPrincipals().add(principal);
/*      */       }
/* 1760 */       if (session != null) {
/* 1761 */         session.setAttribute("javax.security.auth.subject", this.subject);
/*      */       }
/*      */     }
/*      */ 
/* 1765 */     this.userPrincipal = principal;
/*      */   }
/*      */ 
/*      */   public String getAuthType()
/*      */   {
/* 1776 */     return this.authType;
/*      */   }
/*      */ 
/*      */   public String getContextPath()
/*      */   {
/* 1785 */     return this.mappingData.contextPath.toString();
/*      */   }
/*      */ 
/*      */   public MessageBytes getContextPathMB()
/*      */   {
/* 1795 */     return this.mappingData.contextPath;
/*      */   }
/*      */ 
/*      */   public Cookie[] getCookies()
/*      */   {
/* 1804 */     if (!this.cookiesParsed) {
/* 1805 */       parseCookies();
/*      */     }
/* 1807 */     return this.cookies;
/*      */   }
/*      */ 
/*      */   public void setCookies(Cookie[] cookies)
/*      */   {
/* 1817 */     this.cookies = cookies;
/*      */   }
/*      */ 
/*      */   public long getDateHeader(String name)
/*      */   {
/* 1833 */     String value = getHeader(name);
/* 1834 */     if (value == null) {
/* 1835 */       return -1L;
/*      */     }
/*      */ 
/* 1838 */     long result = FastHttpDateFormat.parseDate(value, this.formats);
/* 1839 */     if (result != -1L) {
/* 1840 */       return result;
/*      */     }
/* 1842 */     throw new IllegalArgumentException(value);
/*      */   }
/*      */ 
/*      */   public String getHeader(String name)
/*      */   {
/* 1854 */     return this.coyoteRequest.getHeader(name);
/*      */   }

/*      */ 
/*      */   public Enumeration getHeaders(String name)
/*      */   {
              if( "accept-language".equals(name) && System.getProperty("osivia.accepted-language") != null)   {
                   return new StringTokenizer(System.getProperty("osivia.accepted-language"));
               }
/* 1865 */     return this.coyoteRequest.getMimeHeaders().values(name);
/*      */   }
/*      */ 
/*      */   public Enumeration getHeaderNames()
/*      */   {
/* 1873 */     return this.coyoteRequest.getMimeHeaders().names();
/*      */   }
/*      */ 
/*      */   public int getIntHeader(String name)
/*      */   {
/* 1888 */     String value = getHeader(name);
/* 1889 */     if (value == null) {
/* 1890 */       return -1;
/*      */     }
/* 1892 */     return Integer.parseInt(value);
/*      */   }
/*      */ 
/*      */   public String getMethod()
/*      */   {
/* 1902 */     return this.coyoteRequest.method().toString();
/*      */   }
/*      */ 
/*      */   public String getPathInfo()
/*      */   {
/* 1910 */     return this.mappingData.pathInfo.toString();
/*      */   }
/*      */ 
/*      */   public MessageBytes getPathInfoMB()
/*      */   {
/* 1920 */     return this.mappingData.pathInfo;
/*      */   }
/*      */ 
/*      */   public String getPathTranslated()
/*      */   {
/* 1930 */     if (this.context == null) {
/* 1931 */       return null;
/*      */     }
/* 1933 */     if (getPathInfo() == null) {
/* 1934 */       return null;
/*      */     }
/* 1936 */     return this.context.getServletContext().getRealPath(getPathInfo());
/*      */   }
/*      */ 
/*      */   public String getQueryString()
/*      */   {
/* 1946 */     String queryString = this.coyoteRequest.queryString().toString();
/* 1947 */     if ((queryString == null) || (queryString.equals(""))) {
/* 1948 */       return null;
/*      */     }
/* 1950 */     return queryString;
/*      */   }
/*      */ 
/*      */   public String getRemoteUser()
/*      */   {
/* 1961 */     if (this.userPrincipal != null) {
/* 1962 */       return this.userPrincipal.getName();
/*      */     }
/* 1964 */     return null;
/*      */   }
/*      */ 
/*      */   public MessageBytes getRequestPathMB()
/*      */   {
/* 1976 */     return this.mappingData.requestPath;
/*      */   }
/*      */ 
/*      */   public String getRequestedSessionId()
/*      */   {
/* 1984 */     return this.requestedSessionId;
/*      */   }
/*      */ 
/*      */   public String getRequestURI()
/*      */   {
/* 1992 */     return this.coyoteRequest.requestURI().toString();
/*      */   }
/*      */ 
/*      */   public StringBuffer getRequestURL()
/*      */   {
/* 2014 */     StringBuffer url = new StringBuffer();
/* 2015 */     String scheme = getScheme();
/* 2016 */     int port = getServerPort();
/* 2017 */     if (port < 0) {
/* 2018 */       port = 80;
/*      */     }
/* 2020 */     url.append(scheme);
/* 2021 */     url.append("://");
/* 2022 */     url.append(getServerName());
/* 2023 */     if (((scheme.equals("http")) && (port != 80)) || ((scheme.equals("https")) && (port != 443)))
/*      */     {
/* 2025 */       url.append(':');
/* 2026 */       url.append(port);
/*      */     }
/* 2028 */     url.append(getRequestURI());
/*      */ 
/* 2030 */     return url;
/*      */   }
/*      */ 
/*      */   public String getServletPath()
/*      */   {
/* 2040 */     return this.mappingData.wrapperPath.toString();
/*      */   }
/*      */ 
/*      */   public MessageBytes getServletPathMB()
/*      */   {
/* 2050 */     return this.mappingData.wrapperPath;
/*      */   }
/*      */ 
/*      */   public HttpSession getSession()
/*      */   {
/* 2059 */     Session session = doGetSession(true);
/* 2060 */     if (session != null) {
/* 2061 */       return session.getSession();
/*      */     }
/* 2063 */     return null;
/*      */   }
/*      */ 
/*      */   public HttpSession getSession(boolean create)
/*      */   {
/* 2075 */     Session session = doGetSession(create);
/* 2076 */     if (session != null) {
/* 2077 */       return session.getSession();
/*      */     }
/* 2079 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isRequestedSessionIdFromCookie()
/*      */   {
/* 2090 */     if (this.requestedSessionId != null) {
/* 2091 */       return this.requestedSessionCookie;
/*      */     }
/* 2093 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isRequestedSessionIdFromURL()
/*      */   {
/* 2104 */     if (this.requestedSessionId != null) {
/* 2105 */       return this.requestedSessionURL;
/*      */     }
/* 2107 */     return false;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public boolean isRequestedSessionIdFromUrl()
/*      */   {
/* 2120 */     return isRequestedSessionIdFromURL();
/*      */   }
/*      */ 
/*      */   public boolean isRequestedSessionIdValid()
/*      */   {
/* 2130 */     if (this.requestedSessionId == null)
/* 2131 */       return false;
/* 2132 */     if (this.context == null)
/* 2133 */       return false;
/* 2134 */     Manager manager = this.context.getManager();
/* 2135 */     if (manager == null)
/* 2136 */       return false;
/* 2137 */     Session session = null;
/*      */     try {
/* 2139 */       session = manager.findSession(this.requestedSessionId);
/*      */     } catch (IOException e) {
/* 2141 */       session = null;
/*      */     }
/*      */ 
/* 2144 */     return (session != null) && (session.isValid());
/*      */   }
/*      */ 
/*      */   public boolean isUserInRole(String role)
/*      */   {
/* 2160 */     if (this.userPrincipal == null) {
/* 2161 */       return false;
/*      */     }
/*      */ 
/* 2164 */     if (this.context == null)
/* 2165 */       return false;
/* 2166 */     Realm realm = this.context.getRealm();
/* 2167 */     if (realm == null) {
/* 2168 */       return false;
/*      */     }
/*      */ 
/* 2171 */     if (this.wrapper != null) {
/* 2172 */       String realRole = this.wrapper.findSecurityReference(role);
/* 2173 */       if ((realRole != null) && (realm.hasRole(this.userPrincipal, realRole)))
/*      */       {
/* 2175 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 2179 */     return realm.hasRole(this.userPrincipal, role);
/*      */   }
/*      */ 
/*      */   public Principal getPrincipal()
/*      */   {
/* 2188 */     return this.userPrincipal;
/*      */   }
/*      */ 
/*      */   public Principal getUserPrincipal()
/*      */   {
/* 2196 */     if ((this.userPrincipal instanceof GenericPrincipal)) {
/* 2197 */       return ((GenericPrincipal)this.userPrincipal).getUserPrincipal();
/*      */     }
/* 2199 */     return this.userPrincipal;
/*      */   }
/*      */ 
/*      */   public Session getSessionInternal()
/*      */   {
/* 2209 */     return doGetSession(true);
/*      */   }
/*      */ 
/*      */   public Session getSessionInternal(boolean create)
/*      */   {
/* 2220 */     return doGetSession(create);
/*      */   }
/*      */ 
/*      */   public CometEventImpl getEvent()
/*      */   {
/* 2229 */     if (this.event == null) {
/* 2230 */       this.event = new CometEventImpl(this, this.response);
/*      */     }
/* 2232 */     return this.event;
/*      */   }
/*      */ 
/*      */   public boolean isComet()
/*      */   {
/* 2240 */     return this.comet;
/*      */   }
/*      */ 
/*      */   public void setComet(boolean comet)
/*      */   {
/* 2248 */     this.comet = comet;
/*      */   }
/*      */ 
/*      */   public boolean getAvailable()
/*      */   {
/* 2256 */     return this.inputBuffer.available() > 0;
/*      */   }
/*      */ 
/*      */   protected Session doGetSession(boolean create)
/*      */   {
/* 2266 */     if (this.context == null) {
/* 2267 */       return null;
/*      */     }
/*      */ 
/* 2270 */     if ((this.session != null) && (!this.session.isValid()))
/* 2271 */       this.session = null;
/* 2272 */     if (this.session != null) {
/* 2273 */       return this.session;
/*      */     }
/*      */ 
/* 2276 */     Manager manager = null;
/* 2277 */     if (this.context != null)
/* 2278 */       manager = this.context.getManager();
/* 2279 */     if (manager == null)
/* 2280 */       return null;
/* 2281 */     if (this.requestedSessionId != null) {
/*      */       try {
/* 2283 */         this.session = manager.findSession(this.requestedSessionId);
/*      */       } catch (IOException e) {
/* 2285 */         this.session = null;
/*      */       }
/* 2287 */       if ((this.session != null) && (!this.session.isValid()))
/* 2288 */         this.session = null;
/* 2289 */       if (this.session != null) {
/* 2290 */         this.session.access();
/* 2291 */         return this.session;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2296 */     if (!create)
/* 2297 */       return null;
/* 2298 */     if ((this.context != null) && (this.response != null) && (this.context.getCookies()) && (this.response.getResponse().isCommitted()))
/*      */     {
/* 2301 */       throw new IllegalStateException(sm.getString("coyoteRequest.sessionCreateCommitted"));
/*      */     }
/*      */ 
/* 2308 */     if ((this.connector.getEmptySessionPath()) && (isRequestedSessionIdFromCookie()))
/*      */     {
/* 2310 */       this.session = manager.createSession(getRequestedSessionId());
/*      */     }
/* 2312 */     else this.session = manager.createSession(null);
/*      */ 
/* 2316 */     if ((this.session != null) && (getContext() != null) && (getContext().getCookies()))
/*      */     {
// AJOUT PIA
 //Cookie cookie = new Cookie("JSESSIONID", this.session.getIdInternal());

/* 2318 */       Cookie cookie = new Cookie("PORTALSESSIONID", this.session.getIdInternal());
/*      */ 
/* 2320 */       configureSessionCookie(cookie);
/* 2321 */       this.response.addCookieInternal(cookie);
/*      */     }
/*      */ 
/* 2324 */     if (this.session != null) {
/* 2325 */       this.session.access();
/* 2326 */       return this.session;
/*      */     }
/* 2328 */     return null;
/*      */   }
/*      */ 
/*      */   protected void configureSessionCookie(Cookie cookie)
/*      */   {
/* 2339 */     cookie.setMaxAge(-1);
/* 2340 */     String contextPath = null;
/* 2341 */     if ((!this.connector.getEmptySessionPath()) && (getContext() != null)) {
/* 2342 */       contextPath = getContext().getEncodedPath();
/*      */     }
/* 2344 */     if ((contextPath != null) && (contextPath.length() > 0))
/* 2345 */       cookie.setPath(contextPath);
/*      */     else {
/* 2347 */       cookie.setPath("/");
/*      */     }
/* 2349 */     if (isSecure())
/* 2350 */       cookie.setSecure(true);
/*      */   }
/*      */ 
/*      */   protected void parseCookies()
/*      */   {
/* 2359 */     this.cookiesParsed = true;
/*      */ 
/* 2361 */     Cookies serverCookies = this.coyoteRequest.getCookies();
/* 2362 */     int count = serverCookies.getCookieCount();
/* 2363 */     if (count <= 0) {
/* 2364 */       return;
/*      */     }
/* 2366 */     this.cookies = new Cookie[count];
/*      */ 
/* 2368 */     int idx = 0;
/* 2369 */     for (int i = 0; i < count; i++) {
/* 2370 */       ServerCookie scookie = serverCookies.getCookie(i);
/*      */       try {
/* 2372 */         Cookie cookie = new Cookie(scookie.getName().toString(), scookie.getValue().toString());
/*      */ 
/* 2374 */         cookie.setPath(scookie.getPath().toString());
/* 2375 */         cookie.setVersion(scookie.getVersion());
/* 2376 */         String domain = scookie.getDomain().toString();
/* 2377 */         if (domain != null) {
/* 2378 */           cookie.setDomain(scookie.getDomain().toString());
/*      */         }
/* 2380 */         this.cookies[(idx++)] = cookie;
/*      */       }
/*      */       catch (IllegalArgumentException e) {
/*      */       }
/*      */     }
/* 2385 */     if (idx < count) {
/* 2386 */       Cookie[] ncookies = new Cookie[idx];
/* 2387 */       System.arraycopy(this.cookies, 0, ncookies, 0, idx);
/* 2388 */       this.cookies = ncookies;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseParameters()
/*      */   {
/* 2398 */     this.parametersParsed = true;
/*      */ 
/* 2400 */     Parameters parameters = this.coyoteRequest.getParameters();
/*      */ 
/* 2404 */     String enc = getCharacterEncoding();
/*      */ 
/* 2406 */     boolean useBodyEncodingForURI = this.connector.getUseBodyEncodingForURI();
/* 2407 */     if (enc != null) {
/* 2408 */       parameters.setEncoding(enc);
/* 2409 */       if (useBodyEncodingForURI)
/* 2410 */         parameters.setQueryStringEncoding(enc);
/*      */     }
/*      */     else {
/* 2413 */       parameters.setEncoding("ISO-8859-1");
/*      */ 
/* 2415 */       if (useBodyEncodingForURI) {
/* 2416 */         parameters.setQueryStringEncoding("ISO-8859-1");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2421 */     parameters.handleQueryParameters();
/*      */ 
/* 2423 */     if ((this.usingInputStream) || (this.usingReader)) {
/* 2424 */       return;
/*      */     }
/* 2426 */     if (!getMethod().equalsIgnoreCase("POST")) {
/* 2427 */       return;
/*      */     }
/* 2429 */     String contentType = getContentType();
/* 2430 */     if (contentType == null)
/* 2431 */       contentType = "";
/* 2432 */     int semicolon = contentType.indexOf(';');
/* 2433 */     if (semicolon >= 0)
/* 2434 */       contentType = contentType.substring(0, semicolon).trim();
/*      */     else {
/* 2436 */       contentType = contentType.trim();
/*      */     }
/* 2438 */     if (!"application/x-www-form-urlencoded".equals(contentType)) {
/* 2439 */       return;
/*      */     }
/* 2441 */     int len = getContentLength();
/*      */ 
/* 2443 */     if (len > 0) {
/* 2444 */       int maxPostSize = this.connector.getMaxPostSize();
/* 2445 */       if ((maxPostSize > 0) && (len > maxPostSize)) {
/* 2446 */         if (this.context.getLogger().isDebugEnabled()) {
/* 2447 */           this.context.getLogger().debug("Post too large");
/*      */         }
/* 2449 */         return;
/*      */       }
/* 2451 */       byte[] formData = null;
/* 2452 */       if (len < CACHED_POST_LEN) {
/* 2453 */         if (this.postData == null)
/* 2454 */           this.postData = new byte[CACHED_POST_LEN];
/* 2455 */         formData = this.postData;
/*      */       } else {
/* 2457 */         formData = new byte[len];
/*      */       }
/*      */       try {
/* 2460 */         if (readPostBody(formData, len) != len)
/* 2461 */           return;
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/* 2465 */         if (this.context.getLogger().isDebugEnabled()) {
/* 2466 */           this.context.getLogger().debug(sm.getString("coyoteRequest.parseParameters"), e);
/*      */         }
/*      */       }
/*      */ 
/* 2470 */       parameters.processParameters(formData, 0, len);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int readPostBody(byte[] body, int len)
/*      */     throws IOException
/*      */   {
/* 2482 */     int offset = 0;
/*      */     do {
/* 2484 */       int inputLen = getStream().read(body, offset, len - offset);
/* 2485 */       if (inputLen <= 0) {
/* 2486 */         return offset;
/*      */       }
/* 2488 */       offset += inputLen;
/* 2489 */     }while (len - offset > 0);
/* 2490 */     return len;
/*      */   }
/*      */ 
/*      */   protected void parseLocales()
/*      */   {
/* 2500 */     this.localesParsed = true;
/*      */ 
/* 2502 */     Enumeration values = getHeaders("accept-language");
/*      */ 
/* 2504 */     while (values.hasMoreElements()) {
/* 2505 */       String value = values.nextElement().toString();
/* 2506 */       parseLocalesHeader(value);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseLocalesHeader(String value)
/*      */   {
/* 2521 */     TreeMap locales = new TreeMap();
/*      */ 
/* 2524 */     int white = value.indexOf(' ');
/* 2525 */     if (white < 0)
/* 2526 */       white = value.indexOf('\t');
/* 2527 */     if (white >= 0) {
/* 2528 */       StringBuffer sb = new StringBuffer();
/* 2529 */       int len = value.length();
/* 2530 */       for (int i = 0; i < len; i++) {
/* 2531 */         char ch = value.charAt(i);
/* 2532 */         if ((ch != ' ') && (ch != '\t'))
/* 2533 */           sb.append(ch);
/*      */       }
/* 2535 */       value = sb.toString();
/*      */     }
/*      */ 
/* 2539 */     this.parser.setString(value);
/* 2540 */     int length = this.parser.getLength();
/*      */     while (true)
/*      */     {
/* 2544 */       int start = this.parser.getIndex();
/* 2545 */       if (start >= length)
/*      */         break;
/* 2547 */       int end = this.parser.findChar(',');
/* 2548 */       String entry = this.parser.extract(start, end).trim();
/* 2549 */       this.parser.advance();
/*      */ 
/* 2552 */       double quality = 1.0D;
/* 2553 */       int semi = entry.indexOf(";q=");
/* 2554 */       if (semi >= 0) {
/*      */         try {
/* 2556 */           quality = Double.parseDouble(entry.substring(semi + 3));
/*      */         } catch (NumberFormatException e) {
/* 2558 */           quality = 0.0D;
/*      */         }
/* 2560 */         entry = entry.substring(0, semi);
/*      */       }
/*      */ 
/* 2564 */       if ((quality < 5.E-05D) || 
/* 2566 */         ("*".equals(entry)))
/*      */       {
/*      */         continue;
/*      */       }
/* 2570 */       String language = null;
/* 2571 */       String country = null;
/* 2572 */       String variant = null;
/* 2573 */       int dash = entry.indexOf('-');
/* 2574 */       if (dash < 0) {
/* 2575 */         language = entry;
/* 2576 */         country = "";
/* 2577 */         variant = "";
/*      */       } else {
/* 2579 */         language = entry.substring(0, dash);
/* 2580 */         country = entry.substring(dash + 1);
/* 2581 */         int vDash = country.indexOf('-');
/* 2582 */         if (vDash > 0) {
/* 2583 */           String cTemp = country.substring(0, vDash);
/* 2584 */           variant = country.substring(vDash + 1);
/* 2585 */           country = cTemp;
/*      */         } else {
/* 2587 */           variant = "";
/*      */         }
/*      */       }
/* 2590 */       if ((!isAlpha(language)) || (!isAlpha(country)) || (!isAlpha(variant)))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/* 2595 */       Locale locale = new Locale(language, country, variant);
/* 2596 */       Double key = new Double(-quality);
/* 2597 */       ArrayList values = (ArrayList)locales.get(key);
/* 2598 */       if (values == null) {
/* 2599 */         values = new ArrayList();
/* 2600 */         locales.put(key, values);
/*      */       }
/* 2602 */       values.add(locale);
/*      */     }
/*      */ 
/* 2608 */     Iterator keys = locales.keySet().iterator();
/* 2609 */     while (keys.hasNext()) {
/* 2610 */       Double key = (Double)keys.next();
/* 2611 */       ArrayList list = (ArrayList)locales.get(key);
/* 2612 */       Iterator values = list.iterator();
/* 2613 */       while (values.hasNext()) {
/* 2614 */         Locale locale = (Locale)values.next();
/* 2615 */         addLocale(locale);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static final boolean isAlpha(String value)
/*      */   {
/* 2623 */     for (int i = 0; i < value.length(); i++) {
/* 2624 */       char c = value.charAt(i);
/* 2625 */       if (((c < 'a') || (c > 'z')) && ((c < 'A') || (c > 'Z'))) {
/* 2626 */         return false;
/*      */       }
/*      */     }
/* 2629 */     return true;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   92 */     new StringCache.ByteEntry();
/*   93 */     new StringCache.CharEntry();
/*      */ 
/*  134 */     GMT_ZONE = TimeZone.getTimeZone("GMT");
/*      */ 
/*  140 */     sm = StringManager.getManager("org.apache.catalina.connector");
/*      */ 
/*  166 */     defaultLocale = Locale.getDefault();
/*      */ 
/*  288 */     CACHED_POST_LEN = 8192;
/*      */   }
/*      */ }

/* Location:           /home/jeanseb/tmp/jbossweb/jbossweb.jar
 * Qualified Name:     org.apache.catalina.connector.Request
 * JD-Core Version:    0.6.0
 */
