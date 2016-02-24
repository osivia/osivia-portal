/*     */ package org.apache.tomcat.util.http;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.Enumeration;
/*     */ import org.apache.tomcat.util.buf.MessageBytes;
	  import java.lang.NullPointerException;

/*     */ 
/*     */ public class MimeHeaders
/*     */ {
/*     */   public static final int DEFAULT_HEADER_SIZE = 8;
/* 103 */   private MimeHeaderField[] headers = new MimeHeaderField[8];
/*     */   private int count;
/*     */ 
/*     */   public void recycle()
/*     */   {
/* 122 */     clear();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
	      // PIA : certains cas non identifiés de NullPointer
	      try {
/* 129 */     		for (int i = 0; i < this.count; i++) {
/* 130 */      	 	this.headers[i].recycle();
/*     */     		}
	      } catch( NullPointerException e)	{
		// Pour éviter des plantages en boucle
		// quand la connextion n'est pas recyclee
		this.count = 0;
		throw e;
	      }
/* 132 */     this.count = 0;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 139 */     StringWriter sw = new StringWriter();
/* 140 */     PrintWriter pw = new PrintWriter(sw);
/* 141 */     pw.println("=== MimeHeaders ===");
/* 142 */     Enumeration e = names();
/* 143 */     while (e.hasMoreElements()) {
/* 144 */       String n = (String)e.nextElement();
/* 145 */       pw.println(n + " = " + getHeader(n));
/*     */     }
/* 147 */     return sw.toString();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 156 */     return this.count;
/*     */   }
/*     */ 
/*     */   public MessageBytes getName(int n)
/*     */   {
/* 164 */     return (n >= 0) && (n < this.count) ? this.headers[n].getName() : null;
/*     */   }
/*     */ 
/*     */   public MessageBytes getValue(int n)
/*     */   {
/* 172 */     return (n >= 0) && (n < this.count) ? this.headers[n].getValue() : null;
/*     */   }
/*     */ 
/*     */   public int findHeader(String name, int starting)
/*     */   {
/* 185 */     for (int i = starting; i < this.count; i++) {
/* 186 */       if (this.headers[i].getName().equalsIgnoreCase(name)) {
/* 187 */         return i;
/*     */       }
/*     */     }
/* 190 */     return -1;
/*     */   }
/*     */ 
/*     */   public Enumeration names()
/*     */   {
/* 201 */     return new NamesEnumerator(this);
/*     */   }
/*     */ 
/*     */   public Enumeration values(String name) {
/* 205 */     return new ValuesEnumerator(this, name);
/*     */   }
/*     */ 
/*     */   private MimeHeaderField createHeader()
/*     */   {
/* 217 */     int len = this.headers.length;
/* 218 */     if (this.count >= len)
/*     */     {
/* 220 */       MimeHeaderField[] tmp = new MimeHeaderField[this.count * 2];
/* 221 */       System.arraycopy(this.headers, 0, tmp, 0, len);
/* 222 */       this.headers = tmp;
/*     */     }
/*     */     MimeHeaderField mh;
/* 224 */     if ((mh = this.headers[this.count]) == null)
/*     */     {
/*     */        MimeHeaderField tmp69_66 = new MimeHeaderField(); mh = tmp69_66; this.headers[this.count] = tmp69_66;
/*     */     }
/* 227 */     this.count += 1;
/* 228 */     return mh;
/*     */   }
/*     */ 
/*     */   public MessageBytes addValue(String name)
/*     */   {
/* 235 */     MimeHeaderField mh = createHeader();
/* 236 */     mh.getName().setString(name);
/* 237 */     return mh.getValue();
/*     */   }
/*     */ 
/*     */   public MessageBytes addValue(byte[] b, int startN, int len)
/*     */   {
/* 246 */     MimeHeaderField mhf = createHeader();
/* 247 */     mhf.getName().setBytes(b, startN, len);
/* 248 */     return mhf.getValue();
/*     */   }
/*     */ 
/*     */   public MessageBytes addValue(char[] c, int startN, int len)
/*     */   {
/* 255 */     MimeHeaderField mhf = createHeader();
/* 256 */     mhf.getName().setChars(c, startN, len);
/* 257 */     return mhf.getValue();
/*     */   }
/*     */ 
/*     */   public MessageBytes setValue(String name)
/*     */   {
/* 266 */     for (int i = 0; i < this.count; i++) {
/* 267 */       if (this.headers[i].getName().equalsIgnoreCase(name)) {
/* 268 */         for (int j = i + 1; j < this.count; j++) {
/* 269 */           if (this.headers[j].getName().equalsIgnoreCase(name)) {
/* 270 */             removeHeader(j--);
/*     */           }
/*     */         }
/* 273 */         return this.headers[i].getValue();
/*     */       }
/*     */     }
/* 276 */     MimeHeaderField mh = createHeader();
/* 277 */     mh.getName().setString(name);
/* 278 */     return mh.getValue();
/*     */   }
/*     */ 
/*     */   public MessageBytes getValue(String name)
/*     */   {

		// PIA : certains cas non identifiés de NullPointer
		// On rajoute un try catch avec reinitialisation
		try	{
/* 288 */     		for (int i = 0; i < this.count; i++) {
/* 289 */       	if (this.headers[i].getName().equalsIgnoreCase(name)) {
/* 290 */         		return this.headers[i].getValue();
/*     */       	}
/*     */     		}
		} catch( NullPointerException e)	{
		// Pour éviter des plantages en boucle
		// quand la connextion n'est pas recyclee
		this.count = 0;
		throw e;
		}
/* 293 */     return null;
/*     */   }
/*     */ 
/*     */   public MessageBytes getUniqueValue(String name)
/*     */   {
/* 302 */     MessageBytes result = null;
/* 303 */     for (int i = 0; i < this.count; i++) {
/* 304 */       if (this.headers[i].getName().equalsIgnoreCase(name)) {
/* 305 */         if (result == null)
/* 306 */           result = this.headers[i].getValue();
/*     */         else {
/* 308 */           throw new IllegalArgumentException();
/*     */         }
/*     */       }
/*     */     }
/* 312 */     return result;
/*     */   }
/*     */ 
/*     */   public String getHeader(String name)
/*     */   {
/* 318 */     MessageBytes mh = getValue(name);
/* 319 */     return mh != null ? mh.toString() : null;
/*     */   }
/*     */ 
/*     */   public void removeHeader(String name)
/*     */   {

		// PIA : certains cas non identifiés de NullPointer
		// On rajoute un try catch avec reinitialisation
		try	{

/* 332 */     		for (int i = 0; i < this.count; i++)
/* 333 */       	if (this.headers[i].getName().equalsIgnoreCase(name))
/* 334 */         		removeHeader(i--);
		} catch( NullPointerException e)	{
			// Pour éviter des plantages en boucle
			// quand la connextion n'est pas recyclee
			this.count = 0;
			throw e;
		}

/*     */   }
/*     */ 
/*     */   private void removeHeader(int idx)
/*     */   {
/* 344 */     MimeHeaderField mh = this.headers[idx];
/*     */ 
/* 346 */     mh.recycle();
/* 347 */     this.headers[idx] = this.headers[(this.count - 1)];
/* 348 */     this.headers[(this.count - 1)] = mh;
/* 349 */     this.count -= 1;
/*     */   }
/*     */ }

/* Location:           /home/jeanseb/tmp/jbossweb/jbossweb.jar
 * Qualified Name:     org.apache.tomcat.util.http.MimeHeaders
 * JD-Core Version:    0.6.0
 */
