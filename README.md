** hn-reader

[![Clojars Project](https://img.shields.io/clojars/v/scrapper.svg)](https://clojars.org/scrapper)
[![Dependencies Status](https://jarkeeper.com/agilecreativity/hn-scrapper/status.svg)](https://jarkeeper.com/agilecreativity/hn-scrapper)

Get all of the latest links from [Hacker News](https://news.ycombinator.com/) into a single page.

*** Installation and basic usage as CLI

**** Pre-requisites

- [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Leiningen](http://leiningen.org/#install)

**** Installation

```sh
# Clone this repository locally
mkdir -p ~/projects

git clone https://github.com/agilecreativity/hn-reader.git ~/projects/hn-reader

cd ~/projects/hn-reader

# Create the `~/bin` folder to hold the executable
mkdir -p ~/bin

# Generate the standalone using `lein bin`
lein bin
```

**** Usage

To see the help just type

```sh
~/bin/hn-reader
```

This should give you the help like

```
Extract the lastest Hacker News index to a single file

Usage: hn-reader [options]
  -p, --page-count PAGE-COUNT    20
  -o, --output-file OUTPUT-FILE  hacker-news.org
  -h, --help
Options:

--p PAGE-COUNT  the number of pages to be extracted default to 20
--o OUTPUT-FILE the output file name default to 'hacker-news.org'
```

Now get the list of all news from [Hacker News](https://news.ycombinator.com/news)

```
# Get only the first page from the site
~/bin/hn-scrapper --page-count 1 --output-file hacker-news-front-page.org

# Get all of the news (20 pages) using shorter option
~/bin/hn-scrapper -p 20 -o hacker-news-top-20-pages.org
```

** Example Sessions and Outputs

*** Sample sessions

![](https://github.com/agilecreativity/hn-scrapper/raw/master/doc/01-sample-session.gif)

*** Sample org-mode Output

![](https://github.com/agilecreativity/hn-scrapper/raw/master/doc/02-markdown-output.png)

*** Sample Markdown Output view in Github's Gist

![](https://github.com/agilecreativity/hn-scrapper/raw/master/doc/03-markdown-as-gist.png)

*** The actual result in Org-Mode format

[Sample-markdown-output](doc/04-hacker-news.org)

** Features idea

- Export/print first level content of hackernews to PDFs or Epubs
- Group the results in some ways (topics, keywords, link to YouTube?)
- Persist the result to html pages and store the link just once!

** Useful Links

- [reaver](https://github.com/mischov/reaver)
- [jsoup](https://github.com/jhy/jsoup/)
- [jsoup - selector syntax](https://jsoup.org/cookbook/extracting-data/selector-syntax)
- [record screen as animated gif image](https://www.maketecheasier.com/record-screen-as-animated-gif-ubuntu/)

** License

Copyright © 2016 Burin Choomnuan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
