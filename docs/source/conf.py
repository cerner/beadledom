# -*- coding: utf-8 -*-
from recommonmark.parser import CommonMarkParser

project = u'Beadledom'
copyright = u'2017, Cerner Corporation'
version = '${project.version}'
release = '${project.version}'

# General options
needs_sphinx = '1.0'
master_doc = 'index'
pygments_style = 'tango'
add_function_parentheses = True

extensions = ['sphinx.ext.autodoc', 'sphinx.ext.githubpages']

templates_path = ['_templates']
exclude_trees = ['.build']
source_suffix = ['.rst', '.md']
source_encoding = 'utf-8-sig'
source_parsers = {
    '.md': CommonMarkParser
}

# HTML options
html_theme = 'sphinx_rtd_theme'
html_short_title = "beadledom"
htmlhelp_basename = 'beadledom-doc'
html_use_index = True
html_use_smartypants = True
html_show_sourcelink = False
html_static_path = ['_static']
