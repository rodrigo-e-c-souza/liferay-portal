/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.bookmarks.uad.anonymizer;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
import com.liferay.bookmarks.uad.constants.BookmarksUADConstants;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.user.associated.data.anonymizer.DynamicQueryUADAnonymizer;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noah Sherrill
 */
@Component(
	immediate = true,
	property = "model.class.name=" + BookmarksUADConstants.CLASS_NAME_BOOKMARKS_ENTRY,
	service = UADAnonymizer.class
)
public class BookmarksEntryUADAnonymizer
	extends DynamicQueryUADAnonymizer<BookmarksEntry> {

	@Override
	public void autoAnonymize(
			BookmarksEntry bookmarksEntry, long userId, User anonymousUser)
		throws PortalException {

		if (bookmarksEntry.getStatusByUserId() == userId) {
			bookmarksEntry.setStatusByUserId(anonymousUser.getUserId());
			bookmarksEntry.setStatusByUserName(anonymousUser.getScreenName());
		}

		if (bookmarksEntry.getUserId() == userId) {
			bookmarksEntry.setUserId(anonymousUser.getUserId());
			bookmarksEntry.setUserName(anonymousUser.getFullName());
		}

		_bookmarksEntryLocalService.updateBookmarksEntry(bookmarksEntry);
	}

	@Override
	public void delete(BookmarksEntry bookmarksEntry) throws PortalException {
		_bookmarksEntryLocalService.deleteEntry(bookmarksEntry);
	}

	@Override
	public List<String> getNonanonymizableFieldNames() {
		return Arrays.asList("description", "name", "url");
	}

	@Override
	protected ActionableDynamicQuery doGetActionableDynamicQuery() {
		return _bookmarksEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	protected String[] doGetUserIdFieldNames() {
		return BookmarksUADConstants.USER_ID_FIELD_NAMES_BOOKMARKS_ENTRY;
	}

	@Reference
	private BookmarksEntryLocalService _bookmarksEntryLocalService;

}