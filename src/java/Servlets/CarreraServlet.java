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
@WebServlet(name = "CarreraServlet", urlPatterns = {"/CarreraServlet"})
public class CarreraServlet extends HttpServlet {

    /// Atributos
    Control principal = Control.instance();
    private String carrerasJsonString;
    ArrayList<Carrera> carreras;
    
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
                    carreras = (ArrayList) principal.listarCarreras();
                } catch (GlobalException | NoDataException ex) {
                    Logger.getLogger(CarreraServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                carrerasJsonString = gson.toJson(carreras);
                try {
                    out.println(carrerasJsonString);
                } finally {
                    out.close();
                }
                break;
            case 2: /// Agregar skill
                Carrera carrera = null;
                try {
                    carrera = new Carrera();
                    carrera.setCodigo(request.getParameter("codigo"));
                    carrera.setNombre(request.getParameter("nombre"));
                    carrera.setTitulo(request.getParameter("titulo"));
                    
                    if(insertarCarreras(carrera)){
                        try {
                            //actualiza la lista
                            carreras = (ArrayList)principal.listarCarreras();
                        } catch (GlobalException | NoDataException ex) {
                            out.println("Error al listar.");
                        }

                        carrerasJsonString = gson.toJson(carreras);
                        try {
                            out.println(carrerasJsonString);
                        } finally {
                            out.close();
                        }   
                    }else{
                       out.println("Error al agregar Carrera.");  
                    }
                }catch(Exception e){
                    System.out.println("Error "+e);
                }
            break;
            //Elimina el ultimo estudiante en la lista ya que no tienen identificador unico
            case 3:
                try {
                    String id = request.getParameter("codigo");
                    
                    if(eliminarCarrera(id)){
                        out.println("Carrera eliminada.");
                    }else{
                        out.println("Error al eliminar carrera.");
                    }
                } catch(Exception e) {
                    System.out.println(""+e);
                }
            break;
            case 4: // Modifica
                Carrera carreraedit = null;
                try {
                    carreraedit = new Carrera();
                    carreraedit.setCodigo(request.getParameter("codigo"));
                    carreraedit.setNombre(request.getParameter("nombre"));
                    carreraedit.setTitulo(request.getParameter("titulo"));
                    
                    if(modificarCarreras(carreraedit)){
                        try {
                            /// se modifica la lista
                            carreras = (ArrayList)principal.listarCarreras();
                        } catch (GlobalException | NoDataException ex) {
                            Logger.getLogger(AlumnoServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        carrerasJsonString = gson.toJson(carreras);
                        try {
                            out.println(carrerasJsonString);
                        } finally {
                            out.close();
                        } 
                    }else{
                        out.println("Error al editar carrera.");                      
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
    public boolean insertarCarreras(Carrera c){
        try{          
            principal.insertarCarreras(c);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean eliminarCarrera(String id){
        try{          
            principal.eliminarCarreras(id);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
    public boolean modificarCarreras(Carrera c){
        try{          
            principal.modificarCarreras(c);
            return true;
        }
        catch(GlobalException | NoDataException ex){
            return false;
        }
    }
}
