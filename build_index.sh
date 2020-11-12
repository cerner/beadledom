#!/usr/bin/env bash

url=http://cerner.github.io/beadledom/3.7.1
url_base=${url%\/*}

directories=()

directories+=('<ul>')

echo "Building index.html links for past versions..."

for d in */
do
  if [[ $d =~ ^([0-9]+\.[0-9]+\/)|^([0-9]+\.[0-9]+\.[0-9]+\/) ]]; then
    version="${d%?}"
    url="$url_base/$version/docs"
    directories+=("<li><a href=\"$url\">Beadledom Docs Version $version</a></li>")
  fi
done

directories+=('</ul>')

echo "Moving index.html to index.html.bak"

`mv index.html index.html.bak`

echo "Rebuilding index.html"

cat <<EOF > index.html
  <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  <html lang="en-US">
  <head>
    <meta charset="utf-8">
    <title>Beadledom Site Redirect</title>
    <meta http-equiv="refresh" content="5; url="http://cerner.github.io/beadledom/3.7.1/docs"">
    <script type="text/javascript">
        window.location.replace("http://cerner.github.io/beadledom/3.7.1/docs");
    </script>
  </head>
  <body>
    $(printf "%s\n" "${directories[@]}")
  </body>
  </html>
EOF
