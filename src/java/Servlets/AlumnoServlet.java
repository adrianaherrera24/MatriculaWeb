/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import AccesoDatos.GlobalException;
import AccesoDatos.NoDataException;
import Control.Control;
import LogicaNegocio.Alumno;
import LogicaNegocio.Carrera;
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
@WebServlet(name = "AlumnoServlet", urlPatterns = {"/AlumnoServlet"})
public class AlumnoServlet extends HttpServlet {

    /// Atributos
    Control principal = Control.instance();
    private String alumnosJsonString;
    ArrayList<Alumno> alumnos;
    ArrayList<Carrera> listaCarreras;
    
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
                    alumnos = (ArrayList) principal.listarAlumnos();
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(AlumnoServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                alumnosJsonString = gson.toJson(alumnos);
                try {
                    out.println(alumnosJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar skill
                Alumno alum = null;
                try {
                    alum = new Alumno();
                    alum.setId(request.getParameter("id"));
                    alum.setNombre(request.getParameter("nombre"));
                    alum.setTelefono(request.getParameter("telefono"));
                    alum.setEmail(request.getParameter("email"));
                    alum.setFechaNacimiento(request.getParameter("fecha"));
                    alum.setCarrera(request.getParameter("carrera"));
                    
                    if(insertarAlumnos(alum)){
                        try {
                            //actualiza la lista
                            alumnos = (ArrayList)principal.listarAlumnos();
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                        alumnosJsonString = gson.toJson(alumnos);
                        try {
                            out.println(alumnosJsonString);
                        } finally {
                            out.close();
                        }   
                    }else{
                       out.println("Error al agregar Alumno.");  
                    }
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    String id = request.getParameter("id");
                    
                    if(eliminarAlumno(id)){
                        out.println("Alumno eliminado.");
                    }else{
                        out.println("Error al eliminar alumno.");
                    }
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Alumno alumedit = null;
                try {
                    alumedit = new Alumno();
                    alumedit.setId(request.getParameter("id"));
                    alumedit.setNombre(request.getParameter("nombre"));
                    alumedit.setTelefono(request.getParameter("telefono"));
                    alumedit.setEmail(request.getParameter("email"));
                    alumedit.setFechaNacimiento(request.getParameter("fecha"));
                    alumedit.setCarrera(request.getParameter("carrera"));
                    
                    if(modificarAlumnos(alumedit)){
                        try {
                            /// se modifica la lista
                            alumnos = (ArrayList)principal.listarAlumnos();
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(AlumnoServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        alumnosJsonString = gson.toJson(alumnos);
                        try {
                            out.println(alumnosJsonString);
                        } finally {
                            out.close();
                        } 
                    }else{
                        out.println("Error al editar alumno.");                      
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
    public boolean insertarAlumnos(Alumno a){
        try{          
            principal.insertarAlumnos(a);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean eliminarAlumno(String id){
        try{          
            principal.eliminarAlumnos(id);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean modificarAlumnos(Alumno a){
        try{          
            principal.modificarAlumnos(a);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
