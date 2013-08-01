package org.osivia.portal.administration.ejb;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osivia.portal.administration.util.AdministrationUtils;

/**
 * Dump servlet.
 *
 * @author Cédric Krommenhoek
 * @see HttpServlet
 */
public class DumpServlet extends HttpServlet {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;


    /**
     * Default constructor.
     */
    public DumpServlet() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check administrator privileges
        if (!AdministrationUtils.checkAdminPrivileges(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        response.setContentType("text/plain");
        String fileName = "dump_" + System.currentTimeMillis() + ".txt";
        response.addHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");

        // Stream creation
        PrintWriter out = new PrintWriter(response.getOutputStream());
        this.dumpThread(out);
        out.flush();
        out.close();
    }


    /**
     * Dump thread.
     *
     * @param os output stream
     */
    private void dumpThread(PrintWriter os) {
        // Walk up all the way to the root thread group
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootGroup.getParent()) != null) {
            rootGroup = parent;
        }
        this.listThreads(os, rootGroup);
    }


    /**
     * List all threads and recursively list all subgroup.
     *
     * @param os output stream
     * @param group current thread group
     */
    private void listThreads(PrintWriter os, ThreadGroup group) {
        int nt = group.activeCount();
        Thread[] threads = new Thread[(nt * 2) + 10];
        nt = group.enumerate(threads, false);

        // List every thread in the group
        for (int i = 0; i < nt; i++) {
            Thread t = threads[i];
            os.println("Thread[" + t.getName() + ":" + t.getClass() + "]");

            StackTraceElement[] stack = t.getStackTrace();
            for (StackTraceElement element : stack) {
                os.println("   " + element);
            }

            os.println("       ---------------");
        }

        // Recursively list all subgroups
        int ng = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[(ng * 2) + 10];
        ng = group.enumerate(groups, false);

        for (int i = 0; i < ng; i++) {
            this.listThreads(os, groups[i]);
        }
    }

}
