#!/usr/bin/env python

# ***************************************************************************
# *                       Copyright © 2023 - Stendhal                       *
# ***************************************************************************
# *                                                                         *
# *   This program is free software; you can redistribute it and/or modify  *
# *   it under the terms of the GNU General Public License as published by  *
# *   the Free Software Foundation; either version 2 of the License, or     *
# *   (at your option) any later version.                                   *
# *                                                                         *
# ***************************************************************************


# script for generating data/language/template.txt

import codecs
import errno
import os
import re
import sys


dir_root = os.path.dirname(os.path.dirname(__file__))
dir_config = os.path.normpath(os.path.join(dir_root, "data/conf"))
dir_locale = os.path.normpath(os.path.join(dir_root, "data/languages"))
file_template = os.path.join(dir_locale, "template.txt")


# Helper functions.

def printWarning(msg):
	sys.stdout.write("WARNING: {}\n".format(msg))

def printError(msg):
	sys.stderr.write("ERROR: {}\n".format(msg))

def exitWithError(err, msg):
	printError(msg)
	sys.exit(err)


# categories, names, & descriptions cache
__cache = {
	"items": {},
	"creatures": {}
}


## Parses configuration file for names & descriptions.
def parseConfig(_type, category):
	print("parsing {} names and descriptions from category '{}'".format(_type, category))

	dir_type = os.path.join(dir_config, _type)
	if not os.path.isdir(dir_type):
		printWarning("'{}' directory not found, excluding from translations template".format(dir_type))
		return

	file_xml = os.path.join(dir_type, category + ".xml")
	if not os.path.isfile(file_xml):
		printWarning("'{}' file not found, excluding from translation template".format(file_xml))
		return

	if category not in __cache[_type]:
		__cache[_type][category] = {}
	tmp = __cache[_type][category]

	try:
		fin = codecs.open(file_xml, "r", "utf-8")
		# ensure working with LF line endings
		content = fin.read().replace("\r\n", "\n").replace("\r", "\n")
		fin.close()
	except PermissionError:
		exitWithError(errno.EACCES, "cannot open '{}' for reading, permission denied".format(file_xml))

	# tag to parse from xml
	tag = _type
	if tag.endswith("s"):
		tag = tag[:-1]

	item_name = None
	item_desc = None
	in_comment = False
	for line in content.split("\n"):
		line = line.strip()

		# ignore commented sections
		# NOTE: only ignores lines starting with a comment and ignores content on same line after comment
		if in_comment and "-->" in line:
			in_comment = False
			continue
		if not in_comment:
			in_comment = line.startswith("<!--") and "-->" not in line
		if in_comment:
			continue

		if line.startswith("<{} name=\"".format(tag)):
			item_name = re.sub("^<{} name=\"".format(tag), "", line)
			item_name = re.sub("\".*$", "", item_name).strip()
		elif item_name and line.startswith("<description>"):
			item_desc = re.sub("^<description>", "", line)
			item_desc = re.sub("</description>$", "", item_desc).strip()
		elif item_name and line == "</{}>".format(tag):
			# there may be multiple versions of an entity/item with different descriptions
			if item_name in tmp and item_desc:
				desc_new = item_desc
				# preserve previous description(s)
				item_desc = tmp[item_name]
				# convert to a list for multiple descriptions
				if type(item_desc) == str:
					item_desc = [item_desc]
				elif not item_desc:
					item_desc = []
				# add new description
				item_desc.append(desc_new)

			tmp[item_name] = item_desc

			# reset
			item_name = None
			item_desc = None

	__cache[_type][category] = tmp


## Exports parsed data to template text file.
def exportTemplate():
	if not os.path.isdir(dir_locale):
		exitWithError(errno.ENOENT, "'{}' directory not found, cannot create translation template".format(dir_locale))

	try:
		fout = codecs.open(file_template, "w", "utf-8")
	except PermissionError:
		exitWithError(errno.EACCES, "cannot open '{}' for writing, permission denied".format(file_template))

	# main header
	header = ("", "##", "## Language: <name> (<code>)", "## Translators: <translator1>[, <translator2>...]", "##")
	fout.write("\n".join(header) + "\n")

	for _type in __cache:
		# type header
		fout.write("\n\n# {} names and descriptions".format(_type))

		contents = __cache[_type]
		for category in tuple(sorted(contents)):
			if not contents[category]:
				printWarning("'{}' category '{}' is empty, skipping".format(_type, category))
				continue

			# category header
			fout.write("\n\n## " + category + "\n")

			cat_list = contents[category]
			for item_name in cat_list:
				fout.write("\n" + item_name + "=\n")
				item_desc = cat_list[item_name]
				if item_desc:
					if type(item_desc) == str:
						item_desc = [item_desc]
					for desc in item_desc:
						fout.write(desc + "=\n")

	fout.close()

	print("template exported to '{}'".format(file_template))


## Builds a localization template for translating.
def buildTemplate():
	# categores not translated
	cat_excludes = {
		"items": ("dummy_weapons", "meta")
	}

	for _type in __cache:
		print("parsing type: {}".format(_type))

		category_names = []

		dir_type = os.path.join(dir_config, _type)
		for basename in sorted(os.listdir(dir_type)):
			# include xml configuration files only
			if not os.path.isfile(os.path.join(dir_type, basename)) or not basename.endswith(".xml"):
				continue

			# remove .xml filename suffix
			category = basename[:-4]
			if _type in cat_excludes and category in cat_excludes[_type]:
				continue
			category_names.append(category)

		if len(category_names) == 0:
			printWarning("no '{}' categories found, excluding from translation template".format(_type))

		for category in category_names:
			parseConfig(_type, category)

	exportTemplate()


if __name__ == "__main__":
	# work from source root
	os.chdir(dir_root)

	buildTemplate()
