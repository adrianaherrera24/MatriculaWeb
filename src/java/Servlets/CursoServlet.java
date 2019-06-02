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
import LogicaNegocio.Curso;
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
@WebServlet(name = "CursoServlet", urlPatterns = {"/CursoServlet"})
public class CursoServlet extends HttpServlet {
    
    /// Atributos
    Control principal = Control.instance();
    private String cursosJsonString;
    ArrayList<Curso> cursos;
    
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
                    cursos = (ArrayList) principal.listarCursos();
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(CursoServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                cursosJsonString = gson.toJson(cursos);
                try {
                    out.println(cursosJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar skill
                Curso curso = null;
                try {
                    curso = new Curso();
                    curso.setId(request.getParameter("codigo"));
                    curso.setNombre(request.getParameter("nombre"));
                    curso.setCreditos(Integer.parseInt(request.getParameter("creditos")));
                    curso.setHorasSemanales(Integer.parseInt(request.getParameter("horas")));
                    curso.setCarrera(request.getParameter("carrera"));
                    curso.setCiclo(Integer.parseInt(request.getParameter("ciclo")));
                    curso.setAnno(request.getParameter("anno"));
                    
                    if(insertarCursos(curso)){
                        try {
                            //actualiza la lista
                            cursos = (ArrayList)principal.listarCursos();
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                        cursosJsonString = gson.toJson(cursos);
                        try {
                            out.println(cursosJsonString);
                        } finally {
                            out.close();
                        }   
                    }else{
                       out.println("Error al agregar curso.");  
                    }
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    String id = request.getParameter("codigo");
                    
                    if(eliminarCurso(id)){
                        out.println("Curso eliminado.");
                    }else{
                        out.println("Error al eliminar curso.");
                    }
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Curso cursoedit = null;
                try {
                    cursoedit = new Curso();
                    cursoedit.setId(request.getParameter("codigo"));
                    cursoedit.setNombre(request.getParameter("nombre"));
                    cursoedit.setCreditos(Integer.parseInt(request.getParameter("creditos")));
                    cursoedit.setHorasSemanales(Integer.parseInt(request.getParameter("horas")));
                    cursoedit.setCarrera(request.getParameter("carrera"));
                    cursoedit.setCiclo(Integer.parseInt(request.getParameter("ciclo")));
                    cursoedit.setAnno(request.getParameter("anno"));
                    
                    if(modificarCursos(cursoedit)){
                        try {
                            /// se modifica la lista
                            cursos = (ArrayList)principal.listarCursos();
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(CursoServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        cursosJsonString = gson.toJson(cursos);
                        try {
                            out.println(cursosJsonString);
                        } finally {
                            out.close();
                        } 
                    }else{
                        out.println("Error al editar curso.");                      
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
    public boolean insertarCursos(Curso c){
        try{          
            principal.insertarCursos(c);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean eliminarCurso(String id){
        try{          
            principal.eliminarCursos(id);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean modificarCursos(Curso c){
        try{          
            principal.modificarCursos(c);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
