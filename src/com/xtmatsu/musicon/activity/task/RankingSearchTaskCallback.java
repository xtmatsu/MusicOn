package com.xtmatsu.musicon.activity.task;

import java.util.List;

import com.xtmatsu.musicon.dto.ItemDto;

public interface RankingSearchTaskCallback {
	
	void onSuccess(List<ItemDto> items);

}
