package lk.ijse.dep.poss.business.custom.impl;

import lk.ijse.dep.poss.business.custom.OrderBO;
import lk.ijse.dep.poss.dao.DAOFactory;
import lk.ijse.dep.poss.dao.DAOType;
import lk.ijse.dep.poss.dao.custom.ItemDAO;
import lk.ijse.dep.poss.dao.custom.OrderDetailDAO;
import lk.ijse.dep.poss.dao.custom.OrdersDAO;
import lk.ijse.dep.poss.db.DBConnection;
import lk.ijse.dep.poss.db.HibernateUtil;
import lk.ijse.dep.poss.entity.Item;
import lk.ijse.dep.poss.entity.OrderDetail;
import lk.ijse.dep.poss.entity.Orders;
import org.hibernate.Session;
import org.hibernate.Transaction;
import lk.ijse.dep.poss.util.OrderDetailTM;
import lk.ijse.dep.poss.util.OrderTM;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class OrderBOImpl implements OrderBO { // , Temp

    private OrdersDAO orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);;
    private OrderDetailDAO orderDetailDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER_DETAIL);
    private ItemDAO itemDAO = DAOFactory.getInstance().getDAO(DAOType.ITEM);

    // Interface through injection
/*    @Override
    public void injection() {
        this.orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);
    }  */

    // Setter method injection
/*    private void setOrderDAO(){
        this.orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);
    }*/

    public String getNewOrderId() throws Exception {

        Session session = HibernateUtil.getSessionFactory().openSession();
        orderDAO.setSession(session);
        Transaction tx=null;

        String lastOrderId=null;
        try {
            tx=session.beginTransaction();

            lastOrderId = orderDAO.getLastOrderId();

            tx.commit();
        }catch (Throwable th){
            th.printStackTrace();
            tx.rollback();
        }finally {
            session.clear();
        }



        if (lastOrderId == null) {
            return "OD001";
        } else {
            int maxId = Integer.parseInt(lastOrderId.replace("OD", ""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "OD00" + maxId;
            } else if (maxId < 100) {
                id = "OD0" + maxId;
            } else {
                id = "OD" + maxId;
            }
            return id;
        }
    }

    public void placeOrder(OrderTM order, List<OrderDetailTM> orderDetails) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();

        Session session = HibernateUtil.getSessionFactory().openSession();
        orderDAO.setSession(session);
        orderDetailDAO.setSession(session);
        itemDAO.setSession(session);

        Transaction tx=null;

        try {
            tx=session.beginTransaction();

            orderDAO.save(new Orders(order.getOrderId(),
                    Date.valueOf(order.getOrderDate()),
                    order.getCustomerId()));

            for (OrderDetailTM orderDetail : orderDetails) {
                orderDetailDAO.save(new OrderDetail(
                        order.getOrderId(), orderDetail.getCode(),
                    orderDetail.getQty(), BigDecimal.valueOf(orderDetail.getUnitPrice())
                ));
                Item item = itemDAO.find(orderDetail.getCode());
                item.setQtyOnHand(item.getQtyOnHand() - orderDetail.getQty());
                itemDAO.update(item);
//                new ItemDAOImpl().update(item);
            }

            tx.commit();
        }catch (Throwable th){
            th.printStackTrace();
            tx.rollback();
        }finally {
            session.clear();
        }

//        try {
//            connection.setAutoCommit(false);
//            orderDAO.save(new Orders(order.getOrderId(),
//                    Date.valueOf(order.getOrderDate()),
//                    order.getCustomerId()));
//            for (OrderDetailTM orderDetail : orderDetails) {
//                orderDetailDAO.save(new OrderDetail(
//                        order.getOrderId(), orderDetail.getCode(),
//                        orderDetail.getQty(), BigDecimal.valueOf(orderDetail.getUnitPrice())
//                ));
//
//                Item item = itemDAO.find(orderDetail.getCode());
//                item.setQtyOnHand(item.getQtyOnHand() - orderDetail.getQty());
//                new ItemDAOImpl().update(item);
//
//            }
//            connection.commit();
//            return true;
//        } catch (Throwable throwables) {
//            throwables.printStackTrace();
//            try {
//                connection.rollback();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            return false;
//        } finally {
//            try {
//                connection.setAutoCommit(true);
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        }
    }
}
