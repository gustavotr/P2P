/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;

/**
 *
 * @author Saranghae
 */
public class Arquivo {
    
    private final String nome;
    private ArrayList<Integer> processos;

    public Arquivo(String n) {
        this.nome = n;
    }
    
    public Arquivo(String n, int p) {
        this.nome = n;
        processos = new ArrayList<Integer>();
        processos.add(p);
    }
    
    public void addProcesso(int p){
        processos.add(p);
    }
    
    public ArrayList<Integer> getProcessos(){
        return processos;
    }

    public String getNome() {
        return nome;
    }
    
    
    
}
