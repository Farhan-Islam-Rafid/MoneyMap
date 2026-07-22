package src.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import src.database.DBConnection;
import src.model.DashboardStats;
import src.model.Transaction;
import src.session.Session;


public class TransactionService {


    // ================= ADD TRANSACTION =================

    public void addTransaction(
            String type,
            double amount,
            Date date,
            String note)
            throws SQLException {


        String sql =
                "INSERT INTO transactions "
                + "(user_id,type,amount,trans_date,note) "
                + "VALUES(?,?,?,?,?)";


        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){


            ps.setInt(1, Session.userId);
            ps.setString(2,type);
            ps.setDouble(3,amount);
            ps.setDate(4,date);
            ps.setString(5,note);


            ps.executeUpdate();

        }

    }





    // ================= UPDATE =================

    public void updateTransaction(
            int id,
            String type,
            double amount,
            Date date,
            String note)
            throws SQLException {


        String sql =
                "UPDATE transactions SET "
                +"type=?,amount=?,trans_date=?,note=? "
                +"WHERE id=? AND user_id=?";


        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){


            ps.setString(1,type);
            ps.setDouble(2,amount);
            ps.setDate(3,date);
            ps.setString(4,note);
            ps.setInt(5,id);
            ps.setInt(6,Session.userId);


            ps.executeUpdate();

        }

    }





    // ================= DELETE =================

    public void deleteTransaction(int id)
            throws SQLException {


        String sql =
                "DELETE FROM transactions "
                +"WHERE id=? AND user_id=?";


        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){


            ps.setInt(1,id);
            ps.setInt(2,Session.userId);


            ps.executeUpdate();

        }

    }






    // ================= GET BY ID =================

    public Transaction getTransactionById(int id)
            throws SQLException {


        String sql =
                "SELECT id,type,amount,trans_date,note "
                +"FROM transactions "
                +"WHERE id=? AND user_id=?";


        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){


            ps.setInt(1,id);
            ps.setInt(2,Session.userId);


            ResultSet rs = ps.executeQuery();


            if(rs.next()){


                return new Transaction(

                        rs.getInt("id"),

                        rs.getString("type"),

                        rs.getDouble("amount"),

                        rs.getDate("trans_date"),

                        rs.getString("note")

                );

            }

        }


        return null;

    }







    // ================= ALL TRANSACTIONS =================

    public List<Transaction> getTransactions(
            String searchType,
            String searchYear,
            String searchMonth)
            throws SQLException {


        List<Transaction> list = new ArrayList<>();


        StringBuilder sql =
                new StringBuilder(
                "SELECT id,type,amount,trans_date,note "
                +"FROM transactions "
                +"WHERE user_id=?"
                );


        if(searchType!=null &&
           !searchType.equals("All")){

            sql.append(" AND type=?");

        }


        if(searchYear!=null &&
           !searchYear.isEmpty()){

            sql.append(
            " AND EXTRACT(YEAR FROM trans_date)=?"
            );

        }


        if(searchMonth!=null &&
           !searchMonth.isEmpty()){

            sql.append(
            " AND EXTRACT(MONTH FROM trans_date)=?"
            );

        }



        sql.append(
        " ORDER BY trans_date DESC,id DESC"
        );




        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps =
            conn.prepareStatement(sql.toString())){


            int index=1;


            ps.setInt(index++,Session.userId);



            if(searchType!=null &&
               !searchType.equals("All")){

                ps.setString(index++,searchType);

            }



            if(searchYear!=null &&
               !searchYear.isEmpty()){

                ps.setInt(
                index++,
                Integer.parseInt(searchYear)
                );

            }



            if(searchMonth!=null &&
               !searchMonth.isEmpty()){

                ps.setInt(
                index++,
                Integer.parseInt(searchMonth)
                );

            }



            ResultSet rs=ps.executeQuery();



            while(rs.next()){


                list.add(
                new Transaction(

                    rs.getInt("id"),

                    rs.getString("type"),

                    rs.getDouble("amount"),

                    rs.getDate("trans_date"),

                    rs.getString("note")

                ));

            }

        }


        return list;

    }








    // ================= DASHBOARD SINGLE QUERY =================

    public DashboardStats getDashboardStats(
            LocalDate startDate,
            LocalDate endDate)
            throws SQLException {



        String sql =
        "SELECT "
        +"COALESCE(SUM(CASE WHEN type='Income' THEN amount ELSE 0 END),0) income,"
        +"COALESCE(SUM(CASE WHEN type='Expense' THEN amount ELSE 0 END),0) expense "
        +"FROM transactions "
        +"WHERE user_id=? "
        +"AND trans_date BETWEEN ? AND ?";



        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){


            ps.setInt(1,Session.userId);

            ps.setDate(2,Date.valueOf(startDate));

            ps.setDate(3,Date.valueOf(endDate));


            ResultSet rs=ps.executeQuery();



            if(rs.next()){


                return new DashboardStats(

                    rs.getDouble("income"),

                    rs.getDouble("expense")

                );

            }

        }


        return new DashboardStats(0,0);

    }






    // ================= OLD SUM SUPPORT =================

    public double getSum(
            String type,
            LocalDate start,
            LocalDate end)
            throws SQLException {


        String sql =
        "SELECT COALESCE(SUM(amount),0) total "
        +"FROM transactions "
        +"WHERE type=? AND user_id=?";


        if(start!=null && end!=null){

            sql += " AND trans_date BETWEEN ? AND ?";

        }



        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){


            int i=1;


            ps.setString(i++,type);

            ps.setInt(i++,Session.userId);



            if(start!=null && end!=null){

                ps.setDate(i++,Date.valueOf(start));

                ps.setDate(i++,Date.valueOf(end));

            }



            ResultSet rs=ps.executeQuery();


            if(rs.next()){

                return rs.getDouble("total");

            }

        }


        return 0;

    }

}