package lk.ijse.dep.poss.dao;

import org.hibernate.Session;

public interface SuperDAO <T,ID>{

    public void setSession(Session session);

//    List<T>findAll() throws Exception;
//
//    T find(ID key) throws  Exception;
//
//    boolean save(T lk.ijse.dep.poss.entity) throws Exception;
//
//    boolean update(T lk.ijse.dep.poss.entity) throws Exception;
//
//    boolean delete(ID key) throws Exception;
}
