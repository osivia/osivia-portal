/*     */ package org.apache.catalina.connector;
/*     */ 
/*     */ import java.io.IOException;

import org.apache.catalina.CometEvent;
/*     */ import org.apache.catalina.Context;
/*     */ import org.apache.catalina.Wrapper;
/*     */ import org.apache.catalina.util.StringManager;
import org.apache.commons.lang.StringUtils;
/*     */ import org.apache.coyote.ActionCode;
/*     */ import org.apache.coyote.Adapter;
/*     */ import org.apache.tomcat.util.buf.B2CConverter;
/*     */ import org.apache.tomcat.util.buf.ByteChunk;
/*     */ import org.apache.tomcat.util.buf.CharChunk;
/*     */ import org.apache.tomcat.util.buf.MessageBytes;
/*     */ import org.apache.tomcat.util.http.Cookies;
/*     */ import org.apache.tomcat.util.http.ServerCookie;
/*     */ import org.apache.tomcat.util.net.SocketStatus;
/*     */ import org.jboss.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CoyoteAdapter
/*     */   implements Adapter
/*     */ {
/*  52 */   private static Logger log = Logger.getLogger(CoyoteAdapter.class);
/*     */   public static final int ADAPTER_NOTES = 1;
/*  60 */   protected static final boolean ALLOW_BACKSLASH = Boolean.valueOf(System.getProperty("org.apache.catalina.connector.CoyoteAdapter.ALLOW_BACKSLASH", "false")).booleanValue();
/*     */ 
/*  86 */   private Connector connector = null;
// MODIF PIA
/*     */  // private static final String match = ";jsessionid=";

/*     */   private static final String match = ";portalsessionid=";
/*  99 */   protected StringManager sm = StringManager.getManager("org.apache.catalina.connector");
/*     */ 
/*     */   public CoyoteAdapter(Connector connector)
/*     */   {
/*  75 */     this.connector = connector;
/*     */   }
/*     */ 
/*     */   public boolean event(org.apache.coyote.Request req, org.apache.coyote.Response res, SocketStatus status)
/*     */   {
/* 114 */     Request request = (Request)req.getNote(1);
/* 115 */     Response response = (Response)res.getNote(1);
/*     */ 
/* 117 */     if (request.getWrapper() != null)
/*     */     {
/* 119 */       boolean error = false;
/* 120 */       boolean read = false;
/*     */       try {
/* 122 */         if (status == SocketStatus.OPEN) {
/* 123 */           if (response.isClosed())
/*     */           {
/* 126 */             request.getEvent().setEventType(CometEvent.EventType.END);
/* 127 */             request.getEvent().setEventSubType(null);
/*     */           }
/*     */           else {
/*     */             try {
/* 131 */               if (request.read())
/* 132 */                 read = true;
/*     */             }
/*     */             catch (IOException e) {
/* 135 */               error = true;
/*     */             }
/* 137 */             if (read) {
/* 138 */               request.getEvent().setEventType(CometEvent.EventType.READ);
/* 139 */               request.getEvent().setEventSubType(null);
/* 140 */             } else if (error) {
/* 141 */               request.getEvent().setEventType(CometEvent.EventType.ERROR);
/* 142 */               request.getEvent().setEventSubType(CometEvent.EventSubType.CLIENT_DISCONNECT);
/*     */             } else {
/* 144 */               request.getEvent().setEventType(CometEvent.EventType.END);
/* 145 */               request.getEvent().setEventSubType(null);
/*     */             }
/*     */           }
/* 148 */         } else if (status == SocketStatus.DISCONNECT) {
/* 149 */           request.getEvent().setEventType(CometEvent.EventType.ERROR);
/* 150 */           request.getEvent().setEventSubType(CometEvent.EventSubType.CLIENT_DISCONNECT);
/* 151 */           error = true;
/* 152 */         } else if (status == SocketStatus.ERROR) {
/* 153 */           request.getEvent().setEventType(CometEvent.EventType.ERROR);
/* 154 */           request.getEvent().setEventSubType(CometEvent.EventSubType.IOEXCEPTION);
/* 155 */           error = true;
/* 156 */         } else if (status == SocketStatus.STOP) {
/* 157 */           request.getEvent().setEventType(CometEvent.EventType.END);
/* 158 */           request.getEvent().setEventSubType(CometEvent.EventSubType.SERVER_SHUTDOWN);
/* 159 */         } else if (status == SocketStatus.TIMEOUT) {
/* 160 */           if (response.isClosed())
/*     */           {
/* 163 */             request.getEvent().setEventType(CometEvent.EventType.END);
/* 164 */             request.getEvent().setEventSubType(null);
/*     */           } else {
/* 166 */             request.getEvent().setEventType(CometEvent.EventType.ERROR);
/* 167 */             request.getEvent().setEventSubType(CometEvent.EventSubType.TIMEOUT);
/*     */           }
/*     */         }
/*     */ 
/* 171 */         req.getRequestProcessor().setWorkerThreadName(Thread.currentThread().getName());
/*     */ 
/* 174 */         this.connector.getContainer().getPipeline().getFirst().event(request, response, request.getEvent());
/*     */ 
/* 176 */         if ((!error) && (!response.isClosed()) && (request.getAttribute("javax.servlet.error.exception") != null))
/*     */         {
/* 179 */           request.getEvent().setEventType(CometEvent.EventType.ERROR);
/* 180 */           request.getEvent().setEventSubType(null);
/* 181 */           error = true;
/* 182 */           this.connector.getContainer().getPipeline().getFirst().event(request, response, request.getEvent());
/*     */         }
/* 184 */         if ((response.isClosed()) || (!request.isComet())) {
/* 185 */           res.action(ActionCode.ACTION_COMET_END, null);
/* 186 */         } else if ((!error) && (read) && (request.getAvailable()))
/*     */         {
/* 189 */           request.getEvent().setEventType(CometEvent.EventType.ERROR);
/* 190 */           request.getEvent().setEventSubType(CometEvent.EventSubType.IOEXCEPTION);
/* 191 */           error = true;
/* 192 */           this.connector.getContainer().getPipeline().getFirst().event(request, response, request.getEvent());
/*     */         }
/* 194 */         //int e = !error ? 1 : 0;
/*     */         //return e;

		return !error;
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 196 */         if (!(t instanceof IOException)) {
/* 197 */           log.error(this.sm.getString("coyoteAdapter.service"), t);
/*     */         }
/* 199 */         //error = true;
/* 200 */         //int i = 0;
/*     */         //return i;
return false;
/*     */       }
/*     */       finally
/*     */       {
/* 202 */         req.getRequestProcessor().setWorkerThreadName(null);
/*     */ 
/* 204 */         if ((error) || (response.isClosed()) || (!request.isComet())) {
/* 205 */           request.recycle();
/* 206 */           request.setFilterChain(null);
/* 207 */           response.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 212 */     return false;
/*     */   }
/*     */ 
/*     */   public void service(org.apache.coyote.Request req, org.apache.coyote.Response res)
/*     */     throws Exception
/*     */   {
/* 224 */     Request request = (Request)req.getNote(1);
/* 225 */     Response response = (Response)res.getNote(1);
/*     */ 
/* 227 */     if (request == null)
/*     */     {
/* 230 */       request = this.connector.createRequest();
/* 231 */       request.setCoyoteRequest(req);
/* 232 */       response = this.connector.createResponse();
/* 233 */       response.setCoyoteResponse(res);
/*     */ 
/* 236 */       request.setResponse(response);
/* 237 */       response.setRequest(request);
/*     */ 
/* 240 */       req.setNote(1, request);
/* 241 */       res.setNote(1, response);
/*     */ 
/* 244 */       req.getParameters().setQueryStringEncoding(this.connector.getURIEncoding());
/*     */     }
/*     */ 
/* 249 */     if (this.connector.getXpoweredBy()) {
/* 250 */       response.addHeader("X-Powered-By", "Servlet/2.5");
/*     */     }
/*     */ 
/* 253 */     boolean comet = false;
/*     */     try
/*     */     {
/* 259 */       req.getRequestProcessor().setWorkerThreadName(Thread.currentThread().getName());
/* 260 */       if (postParseRequest(req, request, res, response))
/*     */       {
/* 262 */         this.connector.getContainer().getPipeline().getFirst().invoke(request, response);
/*     */ 
/* 264 */         if (request.isComet()) {
/* 265 */           if ((!response.isClosed()) && (!response.isError())) {
/* 266 */             if (request.getAvailable())
/*     */             {
/* 268 */               if (event(req, res, SocketStatus.OPEN)) {
/* 269 */                 comet = true;
/* 270 */                 res.action(ActionCode.ACTION_COMET_BEGIN, null);
/*     */               }
/*     */             } else {
/* 273 */               comet = true;
/* 274 */               res.action(ActionCode.ACTION_COMET_BEGIN, null);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 279 */             request.setFilterChain(null);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 285 */       if (!comet) {
/* 286 */         response.finishResponse();
/* 287 */         req.action(ActionCode.ACTION_POST_REQUEST, null);
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/*     */     }
/*     */     catch (Throwable t) {
/* 293 */       log.error(this.sm.getString("coyoteAdapter.service"), t);
/*     */     } finally {
/* 295 */       req.getRequestProcessor().setWorkerThreadName(null);
/*     */ 
/* 297 */       if (!comet) {
/* 298 */         request.recycle();
/* 299 */         response.recycle();
/*     */       }
/*     */       else
/*     */       {
/* 303 */         request.clearEncoders();
/* 304 */         response.clearEncoders();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean postParseRequest(org.apache.coyote.Request req, Request request, org.apache.coyote.Response res, Response response)
/*     */     throws Exception
/*     */   {
/* 328 */     if (!req.scheme().isNull())
/*     */     {
/* 330 */       request.setSecure(req.scheme().equals("https"));
/*     */     }
/*     */     else
/*     */     {
/* 334 */       req.scheme().setString(this.connector.getScheme());
/* 335 */       request.setSecure(this.connector.getSecure());
/*     */     }
/*     */ 
/* 343 */     String proxyName = this.connector.getProxyName();
/* 344 */     int proxyPort = this.connector.getProxyPort();
/* 345 */     if (proxyPort != 0) {
/* 346 */       req.setServerPort(proxyPort);
/*     */     }
/* 348 */     if (proxyName != null) {
/* 349 */       req.serverName().setString(proxyName);
/*     */     }
/*     */ 
/* 353 */     parseSessionId(req, request);
/*     */ 
/* 356 */     MessageBytes decodedURI = req.decodedURI();
/* 357 */     decodedURI.duplicate(req.requestURI());
/*     */ 
/* 359 */     if (decodedURI.getType() == 2)
/*     */     {
/* 361 */       ByteChunk uriBB = decodedURI.getByteChunk();
/* 362 */       int semicolon = uriBB.indexOf(';', 0);
/* 363 */       if (semicolon > 0) {
/* 364 */         decodedURI.setBytes(uriBB.getBuffer(), uriBB.getStart(), semicolon);
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 369 */         req.getURLDecoder().convert(decodedURI, false);
/*     */       } catch (IOException ioe) {
/* 371 */         res.setStatus(400);
/* 372 */         res.setMessage("Invalid URI: " + ioe.getMessage());
/* 373 */         return false;
/*     */       }
/*     */ 
/* 376 */       if (!normalize(req.decodedURI())) {
/* 377 */         res.setStatus(400);
/* 378 */         res.setMessage("Invalid URI");
/* 379 */         return false;
/*     */       }
/*     */ 
/* 382 */       convertURI(decodedURI, request);
/*     */     }
/*     */     else
/*     */     {
/* 387 */       decodedURI.toChars();
/*     */ 
/* 389 */       CharChunk uriCC = decodedURI.getCharChunk();
/* 390 */       int semicolon = uriCC.indexOf(';');
/* 391 */       if (semicolon > 0) {
/* 392 */         decodedURI.setChars(uriCC.getBuffer(), uriCC.getStart(), semicolon);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 398 */     String principal = req.getRemoteUser().toString();
/* 399 */     if (principal != null) {
/* 400 */       request.setUserPrincipal(new CoyotePrincipal(principal));
/*     */     }
/*     */ 
/* 404 */     String authtype = req.getAuthType().toString();
/* 405 */     if (authtype != null)
/* 406 */       request.setAuthType(authtype);
/*     */     MessageBytes serverName;
/* 411 */     if (this.connector.getUseIPVHosts()) {
/* 412 */       serverName = req.localName();
/* 413 */       if (serverName.isNull())
/*     */       {
/* 415 */         res.action(ActionCode.ACTION_REQ_LOCAL_NAME_ATTRIBUTE, null);
/*     */       }
/*     */     } else {
/* 418 */       serverName = req.serverName();
/*     */     }
/* 420 */     this.connector.getMapper().map(serverName, decodedURI, request.getMappingData());
/*     */ 
/* 422 */     request.setContext((Context)request.getMappingData().context);
/* 423 */     request.setWrapper((Wrapper)request.getMappingData().wrapper);
/*     */ 
/* 426 */     if ((!this.connector.getAllowTrace()) && (req.method().equalsIgnoreCase("TRACE")))
/*     */     {
/* 428 */       Wrapper wrapper = request.getWrapper();
/* 429 */       String header = null;
/* 430 */       if (wrapper != null) {
/* 431 */         String[] methods = wrapper.getServletMethods();
/* 432 */         if (methods != null) {
/* 433 */           for (int i = 0; i < methods.length; i++) {
/* 434 */             if ("TRACE".equals(methods[i])) {
/*     */               continue;
/*     */             }
/* 437 */             if (header == null)
/* 438 */               header = methods[i];
/*     */             else {
/* 440 */               header = header + ", " + methods[i];
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 445 */       res.setStatus(405);
/* 446 */       res.addHeader("Allow", header);
/* 447 */       res.setMessage("TRACE method is not allowed");
/* 448 */       return false;
/*     */     }
/*     */ 
/* 452 */     MessageBytes redirectPathMB = request.getMappingData().redirectPath;
/* 453 */     if (!redirectPathMB.isNull()) {
/* 454 */       String redirectPath = redirectPathMB.toString();
/* 455 */       String query = request.getQueryString();
/* 456 */       if (request.isRequestedSessionIdFromURL())
/*     */       {
/* 459 */         //redirectPath = redirectPath + ";portalsessionid=" + request.getRequestedSessionId();
/*     */       }
/*     */ 
/* 462 */       if (query != null)
/*     */       {
/* 465 */         redirectPath = redirectPath + "?" + query;
/*     */       }
/* 467 */       response.sendRedirect(redirectPath);
/* 468 */       return false;
/*     */     }
/*     */ 
/* 472 */     parseSessionCookiesId(req, request);
/*     */ 
/* 474 */     return true;
/*     */   }
/*     */ 
/*     */   protected void parseSessionId(org.apache.coyote.Request req, Request request)
/*     */   {
/* 483 */     ByteChunk uriBC = req.requestURI().getByteChunk();
// AJOUT PIA
/* 484 */   //  int semicolon = uriBC.indexOf(";jsessionid=", 0, ";jsessionid=".length(), 0);

/* 484 */     int semicolon = uriBC.indexOf(";portalsessionid=", 0, ";portalsessionid=".length(), 0);
/*     */ 
/* 486 */     if (semicolon > 0)
/*     */     {
/* 489 */       int start = uriBC.getStart();
/* 490 */       int end = uriBC.getEnd();
/*     */ 
// AJOUT PIA
/* 492 */      // int sessionIdStart = semicolon + ";jsessionid=".length();

/* 492 */       int sessionIdStart = semicolon + ";portalsessionid=".length();
/* 493 */       int semicolon2 = uriBC.indexOf(';', sessionIdStart);
/* 494 */       if (semicolon2 >= 0) {
/* 495 */         request.setRequestedSessionId(new String(uriBC.getBuffer(), start + sessionIdStart, semicolon2 - sessionIdStart));
/*     */ 
/* 499 */         byte[] buf = uriBC.getBuffer();
/* 500 */         for (int i = 0; i < end - start - semicolon2; i++) {
/* 501 */           buf[(start + semicolon + i)] = buf[(start + i + semicolon2)];
/*     */         }
/*     */ 
/* 504 */         uriBC.setBytes(buf, start, end - start - semicolon2 + semicolon);
/*     */       } else {
/* 506 */         request.setRequestedSessionId(new String(uriBC.getBuffer(), start + sessionIdStart, end - start - sessionIdStart));
/*     */ 
/* 509 */         uriBC.setEnd(start + semicolon);
/*     */       }
/* 511 */       request.setRequestedSessionURL(true);
/*     */     }
/*     */     else {
/* 514 */       request.setRequestedSessionId(null);
/* 515 */       request.setRequestedSessionURL(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseSessionCookiesId(org.apache.coyote.Request req, Request request)
/*     */   {
/* 527 */     Cookies serverCookies = req.getCookies();
/* 528 */     int count = serverCookies.getCookieCount();
/* 529 */     if (count <= 0) {
/* 530 */       return;
/*     */     }
/* 532 */     for (int i = 0; i < count; i++) {
/* 533 */       ServerCookie scookie = serverCookies.getCookie(i);
// AJOUT PIA
/* 534 */    //   if (!scookie.getName().equals("JSESSIONID"))
/* 534 */       if (!scookie.getName().equals("PORTALSESSIONID"))
/*     */         continue;
/* 536 */       if (!request.isRequestedSessionIdFromCookie())
/*     */       {
					// #1790 LBI check integrity of PORTALSESSIONID
	
					String cookieValue = scookie.getValue().toString();
					
					if(cookieValue != null) {
						
						String[] split = StringUtils.split(cookieValue,".");
						
						// cookie should have a duet of value separated by a dot
						if (split.length != 2) {
							log.error("cookie invalide !" + cookieValue);
							continue;
						}
						// duet of value should be alphanumeric
						if(!StringUtils.isAlphanumeric(split[0]) && !StringUtils.isAlphanumeric(split[1])) {
							log.error("cookie invalide !" + cookieValue);
							continue;							
						}
					}
	
	
/* 538 */         convertMB(scookie.getValue());
/* 539 */         request.setRequestedSessionId(scookie.getValue().toString());
/*     */ 
/* 541 */         request.setRequestedSessionCookie(true);
/* 542 */         request.setRequestedSessionURL(false);
/* 543 */         if (log.isDebugEnabled())
/* 544 */           log.debug(" Requested cookie session id is " + request.getRequestedSessionId());
/*     */       }
/*     */       else {
/* 547 */         if (request.isRequestedSessionIdValid())
/*     */           continue;
/* 549 */         convertMB(scookie.getValue());
/* 550 */         request.setRequestedSessionId(scookie.getValue().toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void convertURI(MessageBytes uri, Request request)
/*     */     throws Exception
/*     */   {
/* 566 */     ByteChunk bc = uri.getByteChunk();
/* 567 */     int length = bc.getLength();
/* 568 */     CharChunk cc = uri.getCharChunk();
/* 569 */     cc.allocate(length, -1);
/*     */ 
/* 571 */     String enc = this.connector.getURIEncoding();
/* 572 */     if (enc != null) {
/* 573 */       B2CConverter conv = request.getURIConverter();
/*     */       try {
/* 575 */         if (conv == null) {
/* 576 */           conv = new B2CConverter(enc);
/* 577 */           request.setURIConverter(conv);
/*     */         } else {
/* 579 */           conv.recycle();
/*     */         }
/*     */       }
/*     */       catch (IOException e) {
/* 583 */         log.error("Invalid URI encoding; using HTTP default");
/* 584 */         this.connector.setURIEncoding(null);
/*     */       }
/* 586 */       if (conv != null) {
/*     */         try {
/* 588 */           conv.convert(bc, cc);
/* 589 */           uri.setChars(cc.getBuffer(), cc.getStart(), cc.getLength());
/*     */ 
/* 591 */           return;
/*     */         } catch (IOException e) {
/* 593 */           log.error("Invalid URI character encoding; trying ascii");
/* 594 */           cc.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 600 */     byte[] bbuf = bc.getBuffer();
/* 601 */     char[] cbuf = cc.getBuffer();
/* 602 */     int start = bc.getStart();
/* 603 */     for (int i = 0; i < length; i++) {
/* 604 */       cbuf[i] = (char)(bbuf[(i + start)] & 0xFF);
/*     */     }
/* 606 */     uri.setChars(cbuf, 0, length);
/*     */   }
/*     */ 
/*     */   protected void convertMB(MessageBytes mb)
/*     */   {
/* 617 */     if (mb.getType() != 2) {
/* 618 */       return;
/*     */     }
/* 620 */     ByteChunk bc = mb.getByteChunk();
/* 621 */     CharChunk cc = mb.getCharChunk();
/* 622 */     int length = bc.getLength();
/* 623 */     cc.allocate(length, -1);
/*     */ 
/* 626 */     byte[] bbuf = bc.getBuffer();
/* 627 */     char[] cbuf = cc.getBuffer();
/* 628 */     int start = bc.getStart();
/* 629 */     for (int i = 0; i < length; i++) {
/* 630 */       cbuf[i] = (char)(bbuf[(i + start)] & 0xFF);
/*     */     }
/* 632 */     mb.setChars(cbuf, 0, length);
/*     */   }
/*     */ 
/*     */   public static boolean normalize(MessageBytes uriMB)
/*     */   {
/* 648 */     ByteChunk uriBC = uriMB.getByteChunk();
/* 649 */     byte[] b = uriBC.getBytes();
/* 650 */     int start = uriBC.getStart();
/* 651 */     int end = uriBC.getEnd();
/*     */ 
/* 654 */     if ((end - start == 1) && (b[start] == 42)) {
/* 655 */       return true;
/*     */     }
/* 657 */     int pos = 0;
/* 658 */     int index = 0;
/*     */ 
/* 662 */     for (pos = start; pos < end; pos++) {
/* 663 */       if (b[pos] == 92) {
/* 664 */         if (ALLOW_BACKSLASH)
/* 665 */           b[pos] = 47;
/*     */         else {
/* 667 */           return false;
/*     */         }
/*     */       }
/* 670 */       if (b[pos] == 0) {
/* 671 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 676 */     if (b[start] != 47) {
/* 677 */       return false;
/*     */     }
/*     */ 
/* 681 */     for (pos = start; pos < end - 1; pos++) {
/* 682 */       if (b[pos] == 47) {
/* 683 */         while ((pos + 1 < end) && (b[(pos + 1)] == 47)) {
/* 684 */           copyBytes(b, pos, pos + 1, end - pos - 1);
/* 685 */           end--;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 693 */     if ((end - start >= 2) && (b[(end - 1)] == 46) && (
/* 694 */       (b[(end - 2)] == 47) || ((b[(end - 2)] == 46) && (b[(end - 3)] == 47))))
/*     */     {
/* 697 */       b[end] = 47;
/* 698 */       end++;
/*     */     }
/*     */ 
/* 702 */     uriBC.setEnd(end);
/*     */ 
/* 704 */     index = 0;
/*     */     while (true)
/*     */     {
/* 708 */       index = uriBC.indexOf("/./", 0, 3, index);
/* 709 */       if (index < 0)
/*     */         break;
/* 711 */       copyBytes(b, start + index, start + index + 2, end - start - index - 2);
/*     */ 
/* 713 */       end -= 2;
/* 714 */       uriBC.setEnd(end);
/*     */     }
/*     */ 
/* 717 */     index = 0;
/*     */     while (true)
/*     */     {
/* 721 */       index = uriBC.indexOf("/../", 0, 4, index);
/* 722 */       if (index < 0) {
/*     */         break;
/*     */       }
/* 725 */       if (index == 0)
/* 726 */         return false;
/* 727 */       int index2 = -1;
/* 728 */       for (pos = start + index - 1; (pos >= 0) && (index2 < 0); pos--) {
/* 729 */         if (b[pos] == 47) {
/* 730 */           index2 = pos;
/*     */         }
/*     */       }
/* 733 */       copyBytes(b, start + index2, start + index + 3, end - start - index - 3);
/*     */ 
/* 735 */       end = end + index2 - index - 3;
/* 736 */       uriBC.setEnd(end);
/* 737 */       index = index2;
/*     */     }
/*     */ 
/* 740 */     uriBC.setBytes(b, start, end);
/*     */ 
/* 742 */     return true;
/*     */   }
/*     */ 
/*     */   protected static void copyBytes(byte[] b, int dest, int src, int len)
/*     */   {
/* 755 */     for (int pos = 0; pos < len; pos++)
/* 756 */       b[(pos + dest)] = b[(pos + src)];
/*     */   }
/*     */ }

/* Location:           /home/jeanseb/tmp/jbossweb/jbossweb.jar
 * Qualified Name:     org.apache.catalina.connector.CoyoteAdapter
 * JD-Core Version:    0.6.0
 */
