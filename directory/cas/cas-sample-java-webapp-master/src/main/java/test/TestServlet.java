/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import eu.europa.ec.mare.usm.information.domain.Context;
import eu.europa.ec.mare.usm.information.domain.Feature;
import eu.europa.ec.mare.usm.information.domain.UserContext;
import eu.europa.ec.mare.usm.information.domain.UserContextQuery;
import eu.europa.ec.mare.usm.information.service.InformationService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author thierry
 */
public class TestServlet extends HttpServlet {

  @EJB
  InformationService informationService;
  
  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
   * methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();
    try {
      /* TODO output your page here. You may use following sample code. */
      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Servlet TestServlet</title>");      
      out.println("</head>");
      out.println("<body>");
      out.println("<h1>Servlet TestServlet at " + request.getContextPath() + "</h1>");
      out.println("<p>getUserPrincipal: " + request.getUserPrincipal() + "</p>");
      out.println("<p>getUserPrincipal.getClass(): " + request.getUserPrincipal().getClass() + "</p>");
      out.println("<p>getRemoteUser: " + request.getRemoteUser() + "</p>");
      out.println("<p>getAuthType: " + request.getAuthType() + "</p>");
      HttpSession session = request.getSession();
      out.println("<h1>HttpSession</h1>");
      Enumeration e = session.getAttributeNames();
      while (e.hasMoreElements()) {
        String key = (String) e.nextElement();
        String val = String.valueOf(session.getAttribute(key));
        out.println("<p>" + key + ": " + val + "</p>");
      }
      if (request.getRemoteUser() != null && informationService != null) {
        UserContextQuery query = new UserContextQuery();
        query.setUserName(request.getRemoteUser());
        UserContext uc = informationService.getUserContext(query);
        if (uc != null) {
          out.println("<h1>UserContext</h1>");
          out.println("<p>getUserName(): " + uc.getUserName() + "</p>");
          out.println("<ul>");
          for (Context c: uc.getContextSet().getContexts()) {
            out.println("<li>Role:" + c.getRole().getRoleName());
            if (c.getScope() != null) {
              out.println(" / Scope:" + c.getScope().getScopeName());
            }
            out.println("</li>");
            out.println("<ul>");
            for (Feature f : c.getRole().getFeatures()) {
              out.println("<li>Application:" + f.getApplicationName() + 
                          " / Feature:" + f.getFeatureName());
            }
            out.println("</ul>");
          }
          out.println("</ul>");
        }
      }
      
      out.println("</body>");
      out.println("</html>");
    } finally {
      out.close();
    }
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

}
