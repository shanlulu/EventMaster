package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendedItems = new ArrayList<>();
		DBConnection conn = DBConnectionFactory.getConnection();
		if (conn == null) {
			return recommendedItems;
		}
		// 1. Get all favorited itemIds
		Set<String> favoriteItemIds = conn.getFavoriteItemIds(userId);
		
		// 2. Get all category frequencies of favorited items
		Map<String, Integer> allCategories = new HashMap<>();
		for (String itemId: favoriteItemIds) {
			Set<String> categories = conn.getCategories(itemId);
			for (String category: categories) {
				if (allCategories.containsKey(category)) {
					allCategories.put(category, allCategories.get(category)+1);
				} else {
					allCategories.put(category, 1);
				}
			}
		}
		
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return Integer.compare(o2.getValue(), o1.getValue());
			}
		});
		
		// 3. Do search based on category, filter out favorited items, sort by distance
		Set<String> visitedItemIds = new HashSet<>();
		for (Entry<String, Integer> entry : categoryList) {
			List<Item> items = conn.searchItems(lat, lon, entry.getKey());
			List<Item> filteredItems = new ArrayList<>();
			for (Item item : items) {
				if (!visitedItemIds.contains(item.getItemId()) && !favoriteItemIds.contains(item.getItemId())) {
					filteredItems.add(item);
					visitedItemIds.add(item.getItemId());
				}
			}
			Collections.sort(filteredItems, new Comparator<Item>() {
				@Override
				public int compare(Item o1, Item o2) {
					return Double.compare(o1.getDistance(), o2.getDistance());
				}
			});
			recommendedItems.addAll(filteredItems);
		}

		conn.close();
		return recommendedItems;
	}
}
