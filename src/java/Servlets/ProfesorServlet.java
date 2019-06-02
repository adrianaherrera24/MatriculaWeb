/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import AccesoDatos.GlobalException;
import AccesoDatos.NoDataException;
import Control.Control;
import LogicaNegocio.Carrera;
import LogicaNegocio.Profesor;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Adriana Herrera
 */
@WebServlet(name = "ProfesorServlet", urlPatterns = {"/ProfesorServlet"})
public class ProfesorServlet extends HttpServlet {

    /// Atributos
    Control principal = Control.instance();
    private String profesoresJsonString;
    ArrayList<Carrera> profesores;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        // Adding new elements to the ArrayList
        String opcion = (String) request.getParameter("opc");
        
        switch (Integer.parseInt(opcion)) {
            //Listar estudiantes
            case 1:
                try {
                    /// obtengo la lista desde el bk
                    profesores = (ArrayList) principal.listarProfesores();
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(ProfesorServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                profesoresJsonString = gson.toJson(profesores);
                try {
                    out.println(profesoresJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar skill
                Profesor prof = null;
                try {
                    prof = new Profesor();
                    prof.setId(request.getParameter("id"));
                    prof.setNombre(request.getParameter("nombre"));
                    prof.setTelefono(request.getParameter("telefono"));
                    prof.setEmail(request.getParameter("email"));
                    
                    if(insertarProfesor(prof)){
                        try {
                            //actualiza la lista
                            profesores = (ArrayList)principal.listarProfesores();
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                        profesoresJsonString = gson.toJson(profesores);
                        try {
                            out.println(profesoresJsonString);
                        } finally {
                            out.close();
                        }   
                    }else{
                       out.println("Error al agregar profesor.");  
                    }
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    String id = request.getParameter("id");
                    
                    if(eliminarProfesor(id)){
                        out.println("Profesor eliminado.");
                    }else{
                        out.println("Error al eliminar profesor.");
                    }
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Profesor profedit = null;
                try {
                    profedit = new Profesor();
                    profedit.setId(request.getParameter("id"));
                    profedit.setNombre(request.getParameter("nombre"));
                    profedit.setTelefono(request.getParameter("telefono"));
                    profedit.setEmail(request.getParameter("email"));
                    
                    if(modificarProfesor(profedit)){
                        try {
                            /// se modifica la lista
                            profesores = (ArrayList)principal.listarProfesores();
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(ProfesorServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        profesoresJsonString = gson.toJson(profesores);
                        try {
                            out.println(profesoresJsonString);
                        } finally {
                            out.close();
                        } 
                    }else{
                        out.println("Error al editar profesor.");                      
                   }
                }catch(Exception e){
                    System.out.println(""+e);
                }
            break;
    }
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
    
    /// Otros
    public boolean insertarProfesor(Profesor p){
        try{          
            principal.insertarProfesores(p);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean eliminarProfesor(String id){
        try{          
            principal.eliminarProfesores(id);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean modificarProfesor(Profesor p){
        try{          
            principal.modificarProfesores(p);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
