/*     */ package org.apache.tomcat.util.http;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import org.apache.tomcat.util.buf.MessageBytes;
/*     */ 
/*     */ class NamesEnumerator
/*     */   implements Enumeration
/*     */ {
/*     */   int pos;
/*     */   int size;
/*     */   String next;
/*     */   MimeHeaders headers;
/*     */ 
/*     */   NamesEnumerator(MimeHeaders headers)
/*     */   {
/* 368 */     this.headers = headers;
/* 369 */     this.pos = 0;
/* 370 */     this.size = headers.size();
/* 371 */     findNext();
/*     */   }
/*     */ 
/*     */   private void findNext() {
/* 375 */     this.next = null;
/* 376 */     for (; this.pos < this.size; this.pos += 1) {
/* 377 */       this.next = this.headers.getName(this.pos).toString();
/* 378 */       for (int j = 0; j < this.pos; j++) {
    // To avoid NPE
    if( this.headers.getName(j) != null)
/* 379 */         if (!this.headers.getName(j).equalsIgnoreCase(this.next))
/*     */           continue;
/* 381 */         this.next = null;
/* 382 */         break;
/*     */       }
/*     */ 
/* 385 */       if (this.next != null)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 392 */     this.pos += 1;
/*     */   }
/*     */ 
/*     */   public boolean hasMoreElements() {
/* 396 */     return this.next != null;
/*     */   }
/*     */ 
/*     */   public Object nextElement() {
/* 400 */     String current = this.next;
/* 401 */     findNext();
/* 402 */     return current;
/*     */   }
/*     */ }

/* Location:           /home/jeanseb/tmp/jbossweb/jbossweb.jar
 * Qualified Name:     org.apache.tomcat.util.http.NamesEnumerator
 * JD-Core Version:    0.6.0
 */