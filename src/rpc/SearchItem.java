package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		// term can be empty
		String keyword = request.getParameter("term");
		
//		TicketMasterAPI tmApi = new TicketMasterAPI();
//		List<Item> items = tmApi.search(lat, lon, keyword);
		
		DBConnection connection = DBConnectionFactory.getConnection();
		List<Item> items = connection.searchItems(lat, lon, keyword);
		Set<String> favorite = connection.getFavoriteItemIds(userId);
		connection.close();
		
		JSONArray array = new JSONArray();
		try {
			for (Item item: items) {
				JSONObject obj = item.toJSONObject();
				
				// Check if this is a favorite one for front end display
				obj.put("favorite", favorite.contains(item.getItemId()));
				array.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RpcHelper.writeJSONArray(response, array);
		
//		String username = "";
//		if (request.getParameter("username") != null) {
//			username = request.getParameter("username");
			// out.print("<h1>Hello " + username + "</h1>");
//		}
		
//		JSONObject obj = new JSONObject();
//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", username));
//			array.put(new JSONObject().put("username", "SHJAHJ"));
//			array.put(new JSONObject().put("username", "RANDOM"));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		// response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
