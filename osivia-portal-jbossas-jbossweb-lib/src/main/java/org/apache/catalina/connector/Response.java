/*      */ package org.apache.catalina.connector;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.MalformedURLException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Vector;
/*      */ import javax.servlet.ServletOutputStream;
/*      */ import javax.servlet.http.Cookie;
/*      */ import javax.servlet.http.HttpServletResponse;
/*      */ import org.apache.catalina.Context;
/*      */ import org.apache.catalina.Globals;
/*      */ import org.apache.catalina.Session;
/*      */ import org.apache.catalina.Wrapper;
/*      */ import org.apache.catalina.security.SecurityUtil;
/*      */ import org.apache.catalina.util.CharsetMapper;
/*      */ import org.apache.catalina.util.StringManager;
/*      */ import org.apache.tomcat.util.buf.CharChunk;
/*      */ import org.apache.tomcat.util.buf.MessageBytes;
/*      */ import org.apache.tomcat.util.buf.UEncoder;
/*      */ import org.apache.tomcat.util.http.FastHttpDateFormat;
/*      */ import org.apache.tomcat.util.http.MimeHeaders;
/*      */ import org.apache.tomcat.util.http.ServerCookie;
/*      */ import org.apache.tomcat.util.net.URL;
/*      */ 
/*      */ public class Response
/*      */   implements HttpServletResponse
/*      */ {
/*      */   protected static final String info = "org.apache.coyote.tomcat5.CoyoteResponse/1.0";
/*      */   protected static StringManager sm;
/*  102 */   protected SimpleDateFormat format = null;
/*      */   protected Connector connector;
/*      */   protected org.apache.coyote.Response coyoteResponse;
/*      */   protected OutputBuffer outputBuffer;
/*      */   protected CoyoteOutputStream outputStream;
/*      */   protected CoyoteWriter writer;
/*  202 */   protected boolean appCommitted = false;
/*      */ 
/*  208 */   protected boolean included = false;
/*      */ 
/*  214 */   private boolean isCharacterEncodingSet = false;
/*      */ 
/*  219 */   protected boolean error = false;
/*      */ 
/*  225 */   protected ArrayList cookies = new ArrayList();
/*      */ 
/*  231 */   protected boolean usingOutputStream = false;
/*      */ 
/*  237 */   protected boolean usingWriter = false;
/*      */ 
/*  243 */   protected UEncoder urlEncoder = new UEncoder();
/*      */ 
/*  249 */   protected CharChunk redirectURLCC = new CharChunk();
/*      */ 
/*  362 */   protected Request request = null;
/*      */ 
/*  384 */   protected ResponseFacade facade = null;
/*      */ 
/*      */   public Response()
/*      */   {
/*   76 */     this.urlEncoder.addSafeCharacter('/');
/*      */   }
/*      */ 
/*      */   public Connector getConnector()
/*      */   {
/*  117 */     return this.connector;
/*      */   }
/*      */ 
/*      */   public void setConnector(Connector connector)
/*      */   {
/*  126 */     this.connector = connector;
/*  127 */     if ("AJP/1.3".equals(connector.getProtocol()))
/*      */     {
/*  129 */       this.outputBuffer = new OutputBuffer(8184);
/*      */     }
/*  131 */     else this.outputBuffer = new OutputBuffer();
/*      */ 
/*  133 */     this.outputStream = new CoyoteOutputStream(this.outputBuffer);
/*  134 */     this.writer = new CoyoteWriter(this.outputBuffer);
/*      */   }
/*      */ 
/*      */   public void setCoyoteResponse(org.apache.coyote.Response coyoteResponse)
/*      */   {
/*  149 */     this.coyoteResponse = coyoteResponse;
/*  150 */     this.outputBuffer.setResponse(coyoteResponse);
/*      */   }
/*      */ 
/*      */   public org.apache.coyote.Response getCoyoteResponse()
/*      */   {
/*  157 */     return this.coyoteResponse;
/*      */   }
/*      */ 
/*      */   public Context getContext()
/*      */   {
/*  165 */     return this.request.getContext();
/*      */   }
/*      */ 
/*      */   public void setContext(Context context)
/*      */   {
/*  177 */     this.request.setContext(context);
/*      */   }
/*      */ 
/*      */   public void recycle()
/*      */   {
/*  261 */     this.outputBuffer.recycle();
/*  262 */     this.usingOutputStream = false;
/*  263 */     this.usingWriter = false;
/*  264 */     this.appCommitted = false;
/*  265 */     this.included = false;
/*  266 */     this.error = false;
/*  267 */     this.isCharacterEncodingSet = false;
/*      */ 
/*  269 */     this.cookies.clear();
/*      */ 
/*  271 */     if ((Globals.IS_SECURITY_ENABLED) || (Connector.RECYCLE_FACADES)) {
/*  272 */       if (this.facade != null) {
/*  273 */         this.facade.clear();
/*  274 */         this.facade = null;
/*      */       }
/*  276 */       if (this.outputStream != null) {
/*  277 */         this.outputStream.clear();
/*  278 */         this.outputStream = null;
/*      */       }
/*  280 */       if (this.writer != null) {
/*  281 */         this.writer.clear();
/*  282 */         this.writer = null;
/*      */       }
/*      */     } else {
/*  285 */       this.writer.recycle();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearEncoders()
/*      */   {
/*  295 */     this.outputBuffer.clearEncoders();
/*      */   }
/*      */ 
/*      */   public int getContentCount()
/*      */   {
/*  306 */     return this.outputBuffer.getContentWritten();
/*      */   }
/*      */ 
/*      */   public void setAppCommitted(boolean appCommitted)
/*      */   {
/*  316 */     this.appCommitted = appCommitted;
/*      */   }
/*      */ 
/*      */   public boolean isAppCommitted()
/*      */   {
/*  324 */     return (this.appCommitted) || (isCommitted()) || (isSuspended()) || ((getContentLength() > 0) && (getContentCount() >= getContentLength()));
/*      */   }
/*      */ 
/*      */   public boolean getIncluded()
/*      */   {
/*  334 */     return this.included;
/*      */   }
/*      */ 
/*      */   public void setIncluded(boolean included)
/*      */   {
/*  345 */     this.included = included;
/*      */   }
/*      */ 
/*      */   public String getInfo()
/*      */   {
/*  355 */     return "org.apache.coyote.tomcat5.CoyoteResponse/1.0";
/*      */   }
/*      */ 
/*      */   public Request getRequest()
/*      */   {
/*  368 */     return this.request;
/*      */   }
/*      */ 
/*      */   public void setRequest(Request request)
/*      */   {
/*  377 */     this.request = request;
/*      */   }
/*      */ 
/*      */   public HttpServletResponse getResponse()
/*      */   {
/*  391 */     if (this.facade == null) {
/*  392 */       this.facade = new ResponseFacade(this);
/*      */     }
/*  394 */     return this.facade;
/*      */   }
/*      */ 
/*      */   public OutputStream getStream()
/*      */   {
/*  402 */     if (this.outputStream == null) {
/*  403 */       this.outputStream = new CoyoteOutputStream(this.outputBuffer);
/*      */     }
/*  405 */     return this.outputStream;
/*      */   }
/*      */ 
/*      */   public void setStream(OutputStream stream)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setSuspended(boolean suspended)
/*      */   {
/*  425 */     this.outputBuffer.setSuspended(suspended);
/*      */   }
/*      */ 
/*      */   public boolean isSuspended()
/*      */   {
/*  433 */     return this.outputBuffer.isSuspended();
/*      */   }
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/*  441 */     return this.outputBuffer.isClosed();
/*      */   }
/*      */ 
/*      */   public void setError()
/*      */   {
/*  449 */     this.error = true;
/*      */   }
/*      */ 
/*      */   public boolean isError()
/*      */   {
/*  457 */     return this.error;
/*      */   }
/*      */ 
/*      */   public ServletOutputStream createOutputStream()
/*      */     throws IOException
/*      */   {
/*  470 */     if (this.outputStream == null) {
/*  471 */       this.outputStream = new CoyoteOutputStream(this.outputBuffer);
/*      */     }
/*  473 */     return this.outputStream;
/*      */   }
/*      */ 
/*      */   public void finishResponse()
/*      */     throws IOException
/*      */   {
/*  486 */     this.outputBuffer.close();
/*      */   }
/*      */ 
/*      */   public int getContentLength()
/*      */   {
/*  494 */     return this.coyoteResponse.getContentLength();
/*      */   }
/*      */ 
/*      */   public String getContentType()
/*      */   {
/*  503 */     return this.coyoteResponse.getContentType();
/*      */   }
/*      */ 
/*      */   public PrintWriter getReporter()
/*      */     throws IOException
/*      */   {
/*  520 */     if (this.outputBuffer.isNew()) {
/*  521 */       this.outputBuffer.checkConverter();
/*  522 */       if (this.writer == null) {
/*  523 */         this.writer = new CoyoteWriter(this.outputBuffer);
/*      */       }
/*  525 */       return this.writer;
/*      */     }
/*  527 */     return null;
/*      */   }
/*      */ 
/*      */   public void flushBuffer()
/*      */     throws IOException
/*      */   {
/*  542 */     this.outputBuffer.flush();
/*      */   }
/*      */ 
/*      */   public int getBufferSize()
/*      */   {
/*  550 */     return this.outputBuffer.getBufferSize();
/*      */   }
/*      */ 
/*      */   public String getCharacterEncoding()
/*      */   {
/*  558 */     return this.coyoteResponse.getCharacterEncoding();
/*      */   }
/*      */ 
/*      */   public ServletOutputStream getOutputStream()
/*      */     throws IOException
/*      */   {
/*  572 */     if (this.usingWriter) {
/*  573 */       throw new IllegalStateException(sm.getString("coyoteResponse.getOutputStream.ise"));
/*      */     }
/*      */ 
/*  576 */     this.usingOutputStream = true;
/*  577 */     if (this.outputStream == null) {
/*  578 */       this.outputStream = new CoyoteOutputStream(this.outputBuffer);
/*      */     }
/*  580 */     return this.outputStream;
/*      */   }
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  589 */     return this.coyoteResponse.getLocale();
/*      */   }
/*      */ 
/*      */   public PrintWriter getWriter()
/*      */     throws IOException
/*      */   {
/*  603 */     if (this.usingOutputStream) {
/*  604 */       throw new IllegalStateException(sm.getString("coyoteResponse.getWriter.ise"));
/*      */     }
/*      */ 
/*  607 */     if (Globals.STRICT_SERVLET_COMPLIANCE)
/*      */     {
/*  620 */       setCharacterEncoding(getCharacterEncoding());
/*      */     }
/*      */ 
/*  623 */     this.usingWriter = true;
/*  624 */     this.outputBuffer.checkConverter();
/*  625 */     if (this.writer == null) {
/*  626 */       this.writer = new CoyoteWriter(this.outputBuffer);
/*      */     }
/*  628 */     return this.writer;
/*      */   }
/*      */ 
/*      */   public boolean isCommitted()
/*      */   {
/*  637 */     return this.coyoteResponse.isCommitted();
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/*  649 */     if (this.included) {
/*  650 */       return;
/*      */     }
/*  652 */     this.coyoteResponse.reset();
/*  653 */     this.outputBuffer.reset();
/*      */   }
/*      */ 
/*      */   public void resetBuffer()
/*      */   {
/*  665 */     if (isCommitted()) {
/*  666 */       throw new IllegalStateException(sm.getString("coyoteResponse.resetBuffer.ise"));
/*      */     }
/*      */ 
/*  669 */     this.outputBuffer.reset();
/*      */   }
/*      */ 
/*      */   public void setBufferSize(int size)
/*      */   {
/*  684 */     if ((isCommitted()) || (!this.outputBuffer.isNew())) {
/*  685 */       throw new IllegalStateException(sm.getString("coyoteResponse.setBufferSize.ise"));
/*      */     }
/*      */ 
/*  688 */     this.outputBuffer.setBufferSize(size);
/*      */   }
/*      */ 
/*      */   public void setContentLength(int length)
/*      */   {
/*  700 */     if (isCommitted()) {
/*  701 */       return;
/*      */     }
/*      */ 
/*  704 */     if (this.included) {
/*  705 */       return;
/*      */     }
/*  707 */     if (this.usingWriter) {
/*  708 */       return;
/*      */     }
/*  710 */     this.coyoteResponse.setContentLength(length);
/*      */   }
/*      */ 
/*      */   public void setContentType(String type)
/*      */   {
/*  722 */     if (isCommitted()) {
/*  723 */       return;
/*      */     }
/*      */ 
/*  726 */     if (this.included) {
/*  727 */       return;
/*      */     }
/*      */ 
/*  730 */     if ((this.usingWriter) && 
/*  731 */       (type != null)) {
/*  732 */       int index = type.indexOf(";");
/*  733 */       if (index != -1) {
/*  734 */         type = type.substring(0, index);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  739 */     this.coyoteResponse.setContentType(type);
/*      */ 
/*  742 */     if (type != null) {
/*  743 */       int index = type.indexOf(";");
/*  744 */       if (index != -1) {
/*  745 */         int len = type.length();
/*  746 */         index++;
/*  747 */         while ((index < len) && (Character.isSpace(type.charAt(index)))) {
/*  748 */           index++;
/*      */         }
/*  750 */         if ((index + 7 < len) && (type.charAt(index) == 'c') && (type.charAt(index + 1) == 'h') && (type.charAt(index + 2) == 'a') && (type.charAt(index + 3) == 'r') && (type.charAt(index + 4) == 's') && (type.charAt(index + 5) == 'e') && (type.charAt(index + 6) == 't') && (type.charAt(index + 7) == '='))
/*      */         {
/*  759 */           this.isCharacterEncodingSet = true;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterEncoding(String charset)
/*      */   {
/*  775 */     if (isCommitted()) {
/*  776 */       return;
/*      */     }
/*      */ 
/*  779 */     if (this.included) {
/*  780 */       return;
/*      */     }
/*      */ 
/*  784 */     if (this.usingWriter) {
/*  785 */       return;
/*      */     }
/*  787 */     this.coyoteResponse.setCharacterEncoding(charset);
/*  788 */     this.isCharacterEncodingSet = true;
/*      */   }
/*      */ 
/*      */   public void setLocale(Locale locale)
/*      */   {
/*  801 */     if (isCommitted()) {
/*  802 */       return;
/*      */     }
/*      */ 
/*  805 */     if (this.included) {
/*  806 */       return;
/*      */     }
/*  808 */     this.coyoteResponse.setLocale(locale);
/*      */ 
/*  812 */     if (this.usingWriter) {
/*  813 */       return;
/*      */     }
/*  815 */     if (this.isCharacterEncodingSet) {
/*  816 */       return;
/*      */     }
/*      */ 
/*  819 */     CharsetMapper cm = getContext().getCharsetMapper();
/*  820 */     String charset = cm.getCharset(locale);
/*  821 */     if (charset != null)
/*  822 */       this.coyoteResponse.setCharacterEncoding(charset);
/*      */   }
/*      */ 
/*      */   public Cookie[] getCookies()
/*      */   {
/*  836 */     return (Cookie[])(Cookie[])this.cookies.toArray(new Cookie[this.cookies.size()]);
/*      */   }
/*      */ 
/*      */   public String getHeader(String name)
/*      */   {
/*  849 */     return this.coyoteResponse.getMimeHeaders().getHeader(name);
/*      */   }
/*      */ 
/*      */   public String[] getHeaderNames()
/*      */   {
/*  859 */     MimeHeaders headers = this.coyoteResponse.getMimeHeaders();
/*  860 */     int n = headers.size();
/*  861 */     String[] result = new String[n];
/*  862 */     for (int i = 0; i < n; i++) {
/*  863 */       result[i] = headers.getName(i).toString();
/*      */     }
/*  865 */     return result;
/*      */   }
/*      */ 
/*      */   public String[] getHeaderValues(String name)
/*      */   {
/*  879 */     Enumeration enumeration = this.coyoteResponse.getMimeHeaders().values(name);
/*  880 */     Vector result = new Vector();
/*  881 */     while (enumeration.hasMoreElements()) {
/*  882 */       result.addElement(enumeration.nextElement());
/*      */     }
/*  884 */     String[] resultArray = new String[result.size()];
/*  885 */     result.copyInto(resultArray);
/*  886 */     return resultArray;
/*      */   }
/*      */ 
/*      */   public String getMessage()
/*      */   {
/*  896 */     return this.coyoteResponse.getMessage();
/*      */   }
/*      */ 
/*      */   public int getStatus()
/*      */   {
/*  904 */     return this.coyoteResponse.getStatus();
/*      */   }
/*      */ 
/*      */   public void reset(int status, String message)
/*      */   {
/*  916 */     reset();
/*  917 */     setStatus(status, message);
/*      */   }
/*      */ 
/*      */   public void addCookie(Cookie cookie)
/*      */   {
/*  933 */     if (this.included) {
/*  934 */       return;
/*      */     }
/*  936 */     addCookieInternal(cookie);
/*      */   }

/*      */   public void addCookieInternal(final Cookie cookie)
/*      */   {
/*  949 */     if (isCommitted()) {
/*  950 */       return;
/*      */     }
/*  952 */     this.cookies.add(cookie);
/*      */ 
/*  954 */     final StringBuffer sb = new StringBuffer();
/*  955 */     if (SecurityUtil.isPackageProtectionEnabled())
/*  956 */       AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Object run() {

			ServerCookie.appendCookieValue
                        (sb, cookie.getVersion(), cookie.getName(), 
                         cookie.getValue(), cookie.getPath(), 
                         cookie.getDomain(), cookie.getComment(), 
			cookie.getMaxAge(), cookie.getSecure());

/*      */ 
/*  963 */           return null;
/*      */         }
/*      */       });
/*  967 */     else ServerCookie.appendCookieValue(sb, cookie.getVersion(), cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.getComment(), cookie.getMaxAge(), cookie.getSecure());
/*      */ 
/*  976 */     addHeader("Set-Cookie", sb.toString());
/*      */   }
/*      */ 
/*      */   public void addDateHeader(String name, long value)
/*      */   {
/*  989 */     if (isCommitted()) {
/*  990 */       return;
/*      */     }
/*      */ 
/*  993 */     if (this.included) {
/*  994 */       return;
/*      */     }
/*      */ 
/*  997 */     if (this.format == null) {
/*  998 */       this.format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
/*      */ 
/* 1000 */       this.format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */     }
/*      */ 
/* 1003 */     addHeader(name, FastHttpDateFormat.formatDate(value, this.format));
/*      */   }
/*      */ 
/*      */   public void addHeader(String name, String value)
/*      */   {
/* 1016 */     if (isCommitted()) {
/* 1017 */       return;
/*      */     }
/*      */ 
/* 1020 */     if (this.included) {
/* 1021 */       return;
/*      */     }
/* 1023 */     this.coyoteResponse.addHeader(name, value);
/*      */   }
/*      */ 
/*      */   public void addIntHeader(String name, int value)
/*      */   {
/* 1036 */     if (isCommitted()) {
/* 1037 */       return;
/*      */     }
/*      */ 
/* 1040 */     if (this.included) {
/* 1041 */       return;
/*      */     }
/* 1043 */     addHeader(name, "" + value);
/*      */   }
/*      */ 
/*      */   public boolean containsHeader(String name)
/*      */   {
/* 1056 */     char cc = name.charAt(0);
/* 1057 */     if ((cc == 'C') || (cc == 'c')) {
/* 1058 */       if (name.equalsIgnoreCase("Content-Type"))
/*      */       {
/* 1060 */         return this.coyoteResponse.getContentType() != null;
/*      */       }
/* 1062 */       if (name.equalsIgnoreCase("Content-Length"))
/*      */       {
/* 1064 */         return this.coyoteResponse.getContentLengthLong() != -1L;
/*      */       }
/*      */     }
/*      */ 
/* 1068 */     return this.coyoteResponse.containsHeader(name);
/*      */   }
/*      */ 
/*      */   public String encodeRedirectURL(String url)
/*      */   {
/* 1080 */     if (isEncodeable(toAbsolute(url))) {
/* 1081 */       return toEncoded(url, this.request.getSessionInternal().getIdInternal());
/*      */     }
/* 1083 */     return url;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public String encodeRedirectUrl(String url)
/*      */   {
/* 1099 */     return encodeRedirectURL(url);
/*      */   }
/*      */ 
/*      */   public String encodeURL(String url)
/*      */   {
/* 1111 */     String absolute = toAbsolute(url);
/* 1112 */     if (isEncodeable(absolute))
/*      */     {
/* 1114 */       if (url.equalsIgnoreCase("")) {
/* 1115 */         url = absolute;
/*      */       }
/* 1117 */       return toEncoded(url, this.request.getSessionInternal().getIdInternal());
/*      */     }
/* 1119 */     return url;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public String encodeUrl(String url)
/*      */   {
/* 1135 */     return encodeURL(url);
/*      */   }
/*      */ 
/*      */   public void sendAcknowledgement()
/*      */     throws IOException
/*      */   {
/* 1147 */     if (isCommitted()) {
/* 1148 */       return;
/*      */     }
/*      */ 
/* 1151 */     if (this.included) {
/* 1152 */       return;
/*      */     }
/* 1154 */     this.coyoteResponse.acknowledge();
/*      */   }
/*      */ 
/*      */   public void sendError(int status)
/*      */     throws IOException
/*      */   {
/* 1171 */     sendError(status, null);
/*      */   }
/*      */ 
/*      */   public void sendError(int status, String message)
/*      */     throws IOException
/*      */   {
/* 1188 */     if (isCommitted()) {
/* 1189 */       throw new IllegalStateException(sm.getString("coyoteResponse.sendError.ise"));
/*      */     }
/*      */ 
/* 1193 */     if (this.included) {
/* 1194 */       return;
/*      */     }
/* 1196 */     Wrapper wrapper = getRequest().getWrapper();
/* 1197 */     if (wrapper != null) {
/* 1198 */       wrapper.incrementErrorCount();
/*      */     }
/*      */ 
/* 1201 */     setError();
/*      */ 
/* 1203 */     this.coyoteResponse.setStatus(status);
/* 1204 */     this.coyoteResponse.setMessage(message);
/*      */ 
/* 1207 */     resetBuffer();
/*      */ 
/* 1210 */     setSuspended(true);
/*      */   }
/*      */ 
/*      */   public void sendRedirect(String location)
/*      */     throws IOException
/*      */   {
/* 1227 */     if (isCommitted()) {
/* 1228 */       throw new IllegalStateException(sm.getString("coyoteResponse.sendRedirect.ise"));
/*      */     }
/*      */ 
/* 1232 */     if (this.included) {
/* 1233 */       return;
/*      */     }
/*      */ 
/* 1236 */     resetBuffer();
/*      */     try
/*      */     {
/* 1240 */       String absolute = toAbsolute(location);
/* 1241 */       setStatus(302);
/* 1242 */       setHeader("Location", absolute);
/*      */     } catch (IllegalArgumentException e) {
/* 1244 */       setStatus(404);
/*      */     }
/*      */ 
/* 1248 */     setSuspended(true);
/*      */   }
/*      */ 
/*      */   public void setDateHeader(String name, long value)
/*      */   {
/* 1261 */     if (isCommitted()) {
/* 1262 */       return;
/*      */     }
/*      */ 
/* 1265 */     if (this.included) {
/* 1266 */       return;
/*      */     }
/*      */ 
/* 1269 */     if (this.format == null) {
/* 1270 */       this.format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
/*      */ 
/* 1272 */       this.format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */     }
/*      */ 
/* 1275 */     setHeader(name, FastHttpDateFormat.formatDate(value, this.format));
/*      */   }
/*      */ 
/*      */   public void setHeader(String name, String value)
/*      */   {
/* 1288 */     if (isCommitted()) {
/* 1289 */       return;
/*      */     }
/*      */ 
/* 1292 */     if (this.included) {
/* 1293 */       return;
/*      */     }
/* 1295 */     this.coyoteResponse.setHeader(name, value);
/*      */   }
/*      */ 
/*      */   public void setIntHeader(String name, int value)
/*      */   {
/* 1308 */     if (isCommitted()) {
/* 1309 */       return;
/*      */     }
/*      */ 
/* 1312 */     if (this.included) {
/* 1313 */       return;
/*      */     }
/* 1315 */     setHeader(name, "" + value);
/*      */   }
/*      */ 
/*      */   public void setStatus(int status)
/*      */   {
/* 1326 */     setStatus(status, null);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setStatus(int status, String message)
/*      */   {
/* 1342 */     if (isCommitted()) {
/* 1343 */       return;
/*      */     }
/*      */ 
/* 1346 */     if (this.included) {
/* 1347 */       return;
/*      */     }
/* 1349 */     this.coyoteResponse.setStatus(status);
/* 1350 */     this.coyoteResponse.setMessage(message);
/*      */   }

/*      */   protected boolean isEncodeable(final String location)
/*      */   {
/* 1373 */     if (location == null) {
/* 1374 */       return false;
/*      */     }
/*      */ 
/* 1377 */     if (location.startsWith("#")) {
/* 1378 */       return false;
/*      */     }
/*      */ 
/* 1381 */     final Request hreq = this.request;
/* 1382 */     final Session session = hreq.getSessionInternal(false);
/* 1383 */     if (session == null)
/* 1384 */       return false;
/* 1385 */     if (hreq.isRequestedSessionIdFromCookie()) {
/* 1386 */       return false;
/*      */     }
/* 1388 */     if (SecurityUtil.isPackageProtectionEnabled()) {
/* 1389 */       return ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Object run()
/*      */         {
/* 1393 */            return new Boolean(doIsEncodeable(hreq, session, location));
/*      */         }
/*      */       })).booleanValue();
/*      */     }
/*      */ 
/* 1397 */     return doIsEncodeable(hreq, session, location);
/*      */   }
/*      */ 
/*      */   private boolean doIsEncodeable(Request hreq, Session session, String location)
/*      */   {
/* 1404 */     URL url = null;
/*      */     try {
/* 1406 */       url = new URL(location);
/*      */     } catch (MalformedURLException e) {
/* 1408 */       return false;
/*      */     }
/*      */ 
/* 1412 */     if (!hreq.getScheme().equalsIgnoreCase(url.getProtocol()))
/* 1413 */       return false;
/* 1414 */     if (!hreq.getServerName().equalsIgnoreCase(url.getHost()))
/* 1415 */       return false;
/* 1416 */     int serverPort = hreq.getServerPort();
/* 1417 */     if (serverPort == -1) {
/* 1418 */       if ("https".equals(hreq.getScheme()))
/* 1419 */         serverPort = 443;
/*      */       else
/* 1421 */         serverPort = 80;
/*      */     }
/* 1423 */     int urlPort = url.getPort();
/* 1424 */     if (urlPort == -1) {
/* 1425 */       if ("https".equals(url.getProtocol()))
/* 1426 */         urlPort = 443;
/*      */       else
/* 1428 */         urlPort = 80;
/*      */     }
/* 1430 */     if (serverPort != urlPort) {
/* 1431 */       return false;
/*      */     }
/* 1433 */     String contextPath = getContext().getPath();
/* 1434 */     if (contextPath != null) {
/* 1435 */       String file = url.getFile();
/* 1436 */       if ((file == null) || (!file.startsWith(contextPath)))
/* 1437 */         return false;
				// MODIF PIA
/* 1438 */      // if (file.indexOf(";jsessionid=" + session.getIdInternal()) >= 0) {
/* 1438 */       if (file.indexOf(";portalsessionid=" + session.getIdInternal()) >= 0) {
/* 1439 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1443 */     return true;
/*      */   }
/*      */ 
/*      */   private String toAbsolute(String location)
/*      */   {
/* 1460 */     if (location == null) {
/* 1461 */       return location;
/*      */     }
/* 1463 */     boolean leadingSlash = location.startsWith("/");
/*      */ 
/* 1465 */     if ((leadingSlash) || (!hasScheme(location)))
/*      */     {
/* 1467 */       this.redirectURLCC.recycle();
/*      */ 
/* 1469 */       String scheme = this.request.getScheme();
/* 1470 */       String name = this.request.getServerName();
/* 1471 */       int port = this.request.getServerPort();
/*      */       try
/*      */       {
/* 1474 */         this.redirectURLCC.append(scheme, 0, scheme.length());
/* 1475 */         this.redirectURLCC.append("://", 0, 3);
/* 1476 */         this.redirectURLCC.append(name, 0, name.length());
/* 1477 */         if (((scheme.equals("http")) && (port != 80)) || ((scheme.equals("https")) && (port != 443)))
/*      */         {
/* 1479 */           this.redirectURLCC.append(':');
/* 1480 */           String portS = port + "";
/* 1481 */           this.redirectURLCC.append(portS, 0, portS.length());
/*      */         }
/* 1483 */         if (!leadingSlash) {
/* 1484 */           String relativePath = this.request.getDecodedRequestURI();
/* 1485 */           int pos = relativePath.lastIndexOf('/');
/* 1486 */           relativePath = relativePath.substring(0, pos);
/*      */ 
/* 1488 */           String encodedURI = null;

/* 1489 */           final String frelativePath = relativePath;
/* 1490 */           if (SecurityUtil.isPackageProtectionEnabled())
/*      */             try {
/* 1492 */               encodedURI = (String)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */               {
/*      */                 public Object run() throws IOException {
/* 1495 */                   return urlEncoder.encodeURL(frelativePath);
/*      */                 } } );
/*      */             } catch (PrivilegedActionException pae) {
/* 1499 */               IllegalArgumentException iae = new IllegalArgumentException(location);
/*      */ 
/* 1501 */               iae.initCause(pae.getException());
/* 1502 */               throw iae;
/*      */             }
/*      */           else {
/* 1505 */             encodedURI = this.urlEncoder.encodeURL(relativePath);
/*      */           }
/* 1507 */           this.redirectURLCC.append(encodedURI, 0, encodedURI.length());
/* 1508 */           this.redirectURLCC.append('/');
/*      */         }
/* 1510 */         this.redirectURLCC.append(location, 0, location.length());
/*      */       } catch (IOException e) {
/* 1512 */         IllegalArgumentException iae = new IllegalArgumentException(location);
/*      */ 
/* 1514 */         iae.initCause(e);
/* 1515 */         throw iae;
/*      */       }
/*      */ 
/* 1518 */       return this.redirectURLCC.toString();
/*      */     }
/*      */ 
/* 1522 */     return location;
/*      */   }
/*      */ 
/*      */   private boolean hasScheme(String uri)
/*      */   {
/* 1533 */     int len = uri.length();
/* 1534 */     for (int i = 0; i < len; i++) {
/* 1535 */       char c = uri.charAt(i);
/* 1536 */       if (c == ':')
/* 1537 */         return i > 0;
/* 1538 */       if (!URL.isSchemeChar(c)) {
/* 1539 */         return false;
/*      */       }
/*      */     }
/* 1542 */     return false;
/*      */   }
/*      */ 
/*      */   protected String toEncoded(String url, String sessionId)
/*      */   {
/* 1554 */     if ((url == null) || (sessionId == null)) {
/* 1555 */       return url;
/*      */     }
/* 1557 */     String path = url;
/* 1558 */     String query = "";
/* 1559 */     String anchor = "";
/* 1560 */     int question = url.indexOf('?');
/* 1561 */     if (question >= 0) {
/* 1562 */       path = url.substring(0, question);
/* 1563 */       query = url.substring(question);
/*      */     }
/* 1565 */     int pound = path.indexOf('#');
/* 1566 */     if (pound >= 0) {
/* 1567 */       anchor = path.substring(pound);
/* 1568 */       path = path.substring(0, pound);
/*      */     }
/* 1570 */     StringBuffer sb = new StringBuffer(path);
/* 1571 */     if (sb.length() > 0) {
/*      */   // MODIF PIA
/* 1572 */       //sb.append(";jsessionid=");
/* 1572 */       //sb.append(";portalsessionid=");
/* 1573 */       //sb.append(sessionId);
/*      */     }
/* 1575 */     sb.append(anchor);
/* 1576 */     sb.append(query);
/* 1577 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   72 */     URL.isSchemeChar('c');
/*      */ 
/*   93 */     sm = StringManager.getManager("org.apache.catalina.connector");
/*      */   }
/*      */ }

/* Location:           /home/jeanseb/tmp/jbossweb/jbossweb.jar
 * Qualified Name:     org.apache.catalina.connector.Response
 * JD-Core Version:    0.6.0
 */
