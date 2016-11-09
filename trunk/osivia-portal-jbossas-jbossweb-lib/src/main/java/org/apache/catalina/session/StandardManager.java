/*     */ package org.apache.catalina.session;
/*     */ 
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyChangeSupport;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletContext;
/*     */ import org.apache.catalina.Container;
/*     */ import org.apache.catalina.Context;
/*     */ import org.apache.catalina.Lifecycle;
/*     */ import org.apache.catalina.LifecycleException;
/*     */ import org.apache.catalina.LifecycleListener;
/*     */ import org.apache.catalina.Loader;
/*     */ import org.apache.catalina.Session;
/*     */ import org.apache.catalina.security.SecurityUtil;
/*     */ import org.apache.catalina.util.CustomObjectInputStream;
/*     */ import org.apache.catalina.util.LifecycleSupport;
/*     */ import org.apache.catalina.util.StringManager;
/*     */ import org.jboss.logging.Logger;
/*     */ 
/*     */ public class StandardManager extends ManagerBase
/*     */   implements Lifecycle, PropertyChangeListener
/*     */ {
/*     */   protected static final String info = "StandardManager/1.0";
/* 108 */   protected LifecycleSupport lifecycle = new LifecycleSupport(this);
/*     */ 
/* 114 */   protected int maxActiveSessions = -1;
/*     */ 
/* 120 */   protected static String name = "StandardManager";
/*     */ 
/* 131 */   protected String pathname = "SESSIONS.ser";
/*     */ 
/* 137 */   protected boolean started = false;
/*     */ 
/* 143 */   protected int rejectedSessions = 0;
/*     */ 
/* 149 */   protected long processingTime = 0L;
/*     */ 
/*     */   public void setContainer(Container container)
/*     */   {
/* 165 */     if ((this.container != null) && ((this.container instanceof Context))) {
/* 166 */       ((Context)this.container).removePropertyChangeListener(this);
/*     */     }
/*     */ 
/* 169 */     super.setContainer(container);
/*     */ 
/* 172 */     if ((this.container != null) && ((this.container instanceof Context))) {
/* 173 */       setMaxInactiveInterval(((Context)this.container).getSessionTimeout() * 60);
/*     */ 
/* 175 */       ((Context)this.container).addPropertyChangeListener(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getInfo()
/*     */   {
/* 188 */     return "StandardManager/1.0";
/*     */   }
/*     */ 
/*     */   public int getMaxActiveSessions()
/*     */   {
/* 199 */     return this.maxActiveSessions;
/*     */   }
/*     */ 
/*     */   public int getRejectedSessions()
/*     */   {
/* 209 */     return this.rejectedSessions;
/*     */   }
/*     */ 
/*     */   public void setRejectedSessions(int rejectedSessions)
/*     */   {
/* 214 */     this.rejectedSessions = rejectedSessions;
/*     */   }
/*     */ 
/*     */   public void setMaxActiveSessions(int max)
/*     */   {
/* 226 */     int oldMaxActiveSessions = this.maxActiveSessions;
/* 227 */     this.maxActiveSessions = max;
/* 228 */     this.support.firePropertyChange("maxActiveSessions", new Integer(oldMaxActiveSessions), new Integer(this.maxActiveSessions));
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 240 */     return name;
/*     */   }
/*     */ 
/*     */   public String getPathname()
/*     */   {
/* 250 */     return this.pathname;
/*     */   }
/*     */ 
/*     */   public void setPathname(String pathname)
/*     */   {
/* 263 */     String oldPathname = this.pathname;
/* 264 */     this.pathname = pathname;
/* 265 */     this.support.firePropertyChange("pathname", oldPathname, this.pathname);
/*     */   }
/*     */ 
/*     */   public Session createSession(String sessionId)
/*     */   {
/* 284 */     if ((this.maxActiveSessions >= 0) && (this.sessions.size() >= this.maxActiveSessions))
/*     */     {
/* 286 */       this.rejectedSessions += 1;
/* 287 */       throw new IllegalStateException(sm.getString("standardManager.createSession.ise"));
/*     */     }
/*     */ 
/* 291 */     return super.createSession(sessionId);
/*     */   }
/*     */ 
/*     */   public void load()
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 306 */     if (SecurityUtil.isPackageProtectionEnabled()) {
/*     */       try {
/* 308 */         AccessController.doPrivileged(new PrivilegedDoLoad());
/*     */       } catch (PrivilegedActionException ex) {
/* 310 */         Exception exception = ex.getException();
/* 311 */         if ((exception instanceof ClassNotFoundException))
/* 312 */           throw ((ClassNotFoundException)exception);
/* 313 */         if ((exception instanceof IOException)) {
/* 314 */           throw ((IOException)exception);
/*     */         }
/* 316 */         if (this.log.isDebugEnabled())
/* 317 */           this.log.debug("Unreported exception in load() " + exception);
/*     */       }
/*     */     }
/*     */     else
/* 321 */       doLoad();
/*     */   }
/*     */ 
/*     */   protected void doLoad()
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 336 */     if (this.log.isDebugEnabled()) {
/* 337 */       this.log.debug("Start: Loading persisted sessions");
/*     */     }
/*     */ 
/* 340 */     this.sessions.clear();
/*     */ 
/* 343 */     File file = file();
/* 344 */     if (file == null)
/* 345 */       return;
/* 346 */     if (this.log.isDebugEnabled())
/* 347 */       this.log.debug(sm.getString("standardManager.loading", this.pathname));
/* 348 */     FileInputStream fis = null;
/* 349 */     ObjectInputStream ois = null;
/* 350 */     Loader loader = null;
/* 351 */     ClassLoader classLoader = null;
/*     */     try {
/* 353 */       fis = new FileInputStream(file.getAbsolutePath());
/* 354 */       BufferedInputStream bis = new BufferedInputStream(fis);
/* 355 */       if (this.container != null)
/* 356 */         loader = this.container.getLoader();
/* 357 */       if (loader != null)
/* 358 */         classLoader = loader.getClassLoader();
/* 359 */       if (classLoader != null) {
/* 360 */         if (this.log.isDebugEnabled())
/* 361 */           this.log.debug("Creating custom object input stream for class loader ");
/* 362 */         ois = new CustomObjectInputStream(bis, classLoader);
/*     */       } else {
/* 364 */         if (this.log.isDebugEnabled())
/* 365 */           this.log.debug("Creating standard object input stream");
/* 366 */         ois = new ObjectInputStream(bis);
/*     */       }
/*     */     } catch (FileNotFoundException e) {
/* 369 */       if (this.log.isDebugEnabled())
/* 370 */         this.log.debug("No persisted data file found");
/* 371 */       return;
/*     */     } catch (IOException e) {
/* 373 */       this.log.error(sm.getString("standardManager.loading.ioe", e), e);
/* 374 */       if (ois != null) {
/*     */         try {
/* 376 */           ois.close();
/*     */         }
/*     */         catch (IOException f) {
/*     */         }
/* 380 */         ois = null;
/*     */       }
/* 382 */       throw e;
/*     */     }
/*     */ 
/* 386 */     synchronized (this.sessions) {
/*     */       try {
/* 388 */         Integer count = (Integer)ois.readObject();
/* 389 */         int n = count.intValue();
/* 390 */         if (this.log.isDebugEnabled())
/* 391 */           this.log.debug("Loading " + n + " persisted sessions");
/* 392 */         for (int i = 0; i < n; i++) {
/* 393 */           StandardSession session = getNewSession();
/* 394 */           session.readObjectData(ois);
/* 395 */           session.setManager(this);
/* 396 */           this.sessions.put(session.getIdInternal(), session);
/* 397 */           session.activate();
/*     */         }
/*     */       } catch (ClassNotFoundException e) {
/* 400 */         this.log.error(sm.getString("standardManager.loading.cnfe", e), e);
/* 401 */         if (ois != null) {
/*     */           try {
/* 403 */             ois.close();
/*     */           }
/*     */           catch (IOException f) {
/*     */           }
/* 407 */           ois = null;
/*     */         }
/* 409 */         throw e;
/*     */       } catch (IOException e) {
/* 411 */         this.log.error(sm.getString("standardManager.loading.ioe", e), e);
/* 412 */         if (ois != null) {
/*     */           try {
/* 414 */             ois.close();
/*     */           }
/*     */           catch (IOException f) {
/*     */           }
/* 418 */           ois = null;
/*     */         }
/* 420 */         throw e;
/*     */       }
/*     */       finally {
/*     */         try {
/* 424 */           if (ois != null) {
/* 425 */             ois.close();
/*     */           }
/*     */         }
/*     */         catch (IOException f)
/*     */         {
/*     */         }
/* 431 */         if ((file != null) && (file.exists())) {
/* 432 */           file.delete();
/*     */         }
/*     */       }
/*     */     }
/* 436 */     if (this.log.isDebugEnabled())
/* 437 */       this.log.debug("Finish: Loading persisted sessions");
/*     */   }
/*     */ 
/*     */   public void unload()
/*     */     throws IOException
/*     */   {
/* 449 */     if (SecurityUtil.isPackageProtectionEnabled()) {
/*     */       try {
/* 451 */         AccessController.doPrivileged(new PrivilegedDoUnload());
/*     */       } catch (PrivilegedActionException ex) {
/* 453 */         Exception exception = ex.getException();
/* 454 */         if ((exception instanceof IOException)) {
/* 455 */           throw ((IOException)exception);
/*     */         }
/* 457 */         if (this.log.isDebugEnabled())
/* 458 */           this.log.debug("Unreported exception in unLoad() " + exception);
/*     */       }
/*     */     }
/*     */     else
/* 462 */       doUnload();
/*     */   }
/*     */ 
/*     */   protected void doUnload()
/*     */     throws IOException
/*     */   {
/* 476 */     if (this.log.isDebugEnabled()) {
/* 477 */       this.log.debug("Unloading persisted sessions");
/*     */     }
/*     */ 
/* 480 */     File file = file();
/* 481 */     if (file == null)
/* 482 */       return;
/* 483 */     if (this.log.isDebugEnabled())
/* 484 */       this.log.debug(sm.getString("standardManager.unloading", this.pathname));
/* 485 */     FileOutputStream fos = null;
/* 486 */     ObjectOutputStream oos = null;
/*     */     try {
/* 488 */       fos = new FileOutputStream(file.getAbsolutePath());
/* 489 */       oos = new ObjectOutputStream(new BufferedOutputStream(fos));
/*     */     } catch (IOException e) {
/* 491 */       this.log.error(sm.getString("standardManager.unloading.ioe", e), e);
/* 492 */       if (oos != null) {
/*     */         try {
/* 494 */           oos.close();
/*     */         }
/*     */         catch (IOException f) {
/*     */         }
/* 498 */         oos = null;
/*     */       }
/* 500 */       throw e;
/*     */     }
/*     */ 
/* 504 */     ArrayList list = new ArrayList();
/* 505 */     synchronized (this.sessions) {
/* 506 */       if (this.log.isDebugEnabled())
/* 507 */         this.log.debug("Unloading " + this.sessions.size() + " sessions");
/*     */       try {
/* 509 */         oos.writeObject(new Integer(this.sessions.size()));
/* 510 */         Iterator elements = this.sessions.values().iterator();
/* 511 */         while (elements.hasNext()) {
/* 512 */           StandardSession session = (StandardSession)elements.next();
/*     */ 
/* 514 */           list.add(session);
/* 515 */           session.passivate();
/* 516 */           session.writeObjectData(oos);
/*     */         }
/*     */       } catch (IOException e) {
/* 519 */         this.log.error(sm.getString("standardManager.unloading.ioe", e), e);
/* 520 */         if (oos != null) {
/*     */           try {
/* 522 */             oos.close();
/*     */           }
/*     */           catch (IOException f) {
/*     */           }
/* 526 */           oos = null;
/*     */         }
/* 528 */         throw e;
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 534 */       oos.flush();
/* 535 */       oos.close();
/* 536 */       oos = null;
/*     */     } catch (IOException e) {
/* 538 */       if (oos != null) {
/*     */         try {
/* 540 */           oos.close();
/*     */         }
/*     */         catch (IOException f) {
/*     */         }
/* 544 */         oos = null;
/*     */       }
/* 546 */       throw e;
/*     */     }
/*     */ 
/* 550 */     if (this.log.isDebugEnabled())
/* 551 */       this.log.debug("Expiring " + list.size() + " persisted sessions");
/* 552 */     Iterator expires = list.iterator();
/* 553 */     while (expires.hasNext()) {
/* 554 */       StandardSession session = (StandardSession)expires.next();
/*     */       try {
/* 556 */         session.expire(false);
/*     */       } catch (Throwable t) {
/*     */       }
/*     */       finally {
/* 560 */         session.recycle();
/*     */       }
/*     */     }
/*     */ 
/* 564 */     if (this.log.isDebugEnabled())
/* 565 */       this.log.debug("Unloading complete");
/*     */   }
/*     */ 
/*     */   public void addLifecycleListener(LifecycleListener listener)
/*     */   {
/* 580 */     this.lifecycle.addLifecycleListener(listener);
/*     */   }
/*     */ 
/*     */   public LifecycleListener[] findLifecycleListeners()
/*     */   {
/* 591 */     return this.lifecycle.findLifecycleListeners();
/*     */   }
/*     */ 
/*     */   public void removeLifecycleListener(LifecycleListener listener)
/*     */   {
/* 603 */     this.lifecycle.removeLifecycleListener(listener);
/*     */   }
/*     */ 
/*     */   public void start()
/*     */     throws LifecycleException
/*     */   {
/* 617 */     if (!this.initialized) {
/* 618 */       init();
/*     */     }
/*     */ 
/* 621 */     if (this.started) {
/* 622 */       return;
/*     */     }
/* 624 */     this.lifecycle.fireLifecycleEvent("start", null);
/* 625 */     this.started = true;
/*     */ 
/* 628 */     if (this.log.isDebugEnabled())
/* 629 */       this.log.debug("Force random number initialization starting");
/* 630 */     String dummy = generateSessionId();
/* 631 */     if (this.log.isDebugEnabled()) {
/* 632 */       this.log.debug("Force random number initialization completed");
/*     */     }
/*     */     try
/*     */     {
/* 636 */       load();
/*     */     } catch (Throwable t) {
/* 638 */       this.log.error(sm.getString("standardManager.managerLoad"), t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */     throws LifecycleException
/*     */   {
/* 654 */     if (this.log.isDebugEnabled()) {
/* 655 */       this.log.debug("Stopping");
/*     */     }
/*     */ 
/* 658 */     if (!this.started)
/* 659 */       return;
/* 660 */     this.lifecycle.fireLifecycleEvent("stop", null);
/* 661 */     this.started = false;
/*     */     try
/*     */     {
/* 665 */       unload();
/*     */     } catch (Throwable t) {
/* 667 */       this.log.error(sm.getString("standardManager.managerUnload"), t);
/*     */     }
/*     */ 
/* 671 */     Session[] sessions = findSessions();
/* 672 */     for (int i = 0; i < sessions.length; i++) {
/* 673 */       Session session = sessions[i];
/*     */       try {
/* 675 */         if (session.isValid())    {
    if( !"/portal".equals(session.getSession().getServletContext().getContextPath()))
//    log.error("stop session" + session.getSession().getServletContext().getContextPath());
/* 676 */           session.expire();
                    }
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */       finally
/*     */       {
/* 683 */         session.recycle();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 688 */     this.random = null;
/*     */ 
/* 690 */     if (this.initialized)
/* 691 */       destroy();
/*     */   }
/*     */ 
/*     */   public void propertyChange(PropertyChangeEvent event)
/*     */   {
/* 707 */     if (!(event.getSource() instanceof Context))
/* 708 */       return;
/* 709 */     Context context = (Context)event.getSource();
/*     */ 
/* 712 */     if (event.getPropertyName().equals("sessionTimeout"))
/*     */       try {
/* 714 */         setMaxInactiveInterval(((Integer)event.getNewValue()).intValue() * 60);
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 717 */         this.log.error(sm.getString("standardManager.sessionTimeout", event.getNewValue().toString()));
/*     */       }
/*     */   }
/*     */ 
/*     */   protected File file()
/*     */   {
/* 734 */     if ((this.pathname == null) || (this.pathname.length() == 0))
/* 735 */       return null;
/* 736 */     File file = new File(this.pathname);
/* 737 */     if ((!file.isAbsolute()) && 
/* 738 */       ((this.container instanceof Context))) {
/* 739 */       ServletContext servletContext = ((Context)this.container).getServletContext();
/*     */ 
/* 741 */       File tempdir = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
/*     */ 
/* 743 */       if (tempdir != null) {
/* 744 */         file = new File(tempdir, this.pathname);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 749 */     return file;
/*     */   }
/*     */ 
/*     */   private class PrivilegedDoUnload
/*     */     implements PrivilegedExceptionAction
/*     */   {
/*     */     PrivilegedDoUnload()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object run()
/*     */       throws Exception
/*     */     {
/*  89 */       StandardManager.this.doUnload();
/*  90 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PrivilegedDoLoad
/*     */     implements PrivilegedExceptionAction
/*     */   {
/*     */     PrivilegedDoLoad()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object run()
/*     */       throws Exception
/*     */     {
/*  77 */       StandardManager.this.doLoad();
/*  78 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/jeanseb/tmp/jbossweb/jbossweb.jar
 * Qualified Name:     org.apache.catalina.session.StandardManager
 * JD-Core Version:    0.6.0
 */