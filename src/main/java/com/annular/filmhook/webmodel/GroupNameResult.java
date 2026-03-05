package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class GroupNameResult {

	private String groupName;
	private List<Integer> userIds;

}
