#!/usr/bin/env bash

url=${beadledom.url}
url_base=${url%\/*}

dirs=()

dirs+=('<ul>')

echo "Building index.html links for past versions..."

for d in */
do
  if [[ $d =~ ^([0-9]+\.[0-9]+\/)|^([0-9]+\.[0-9]+\.[0-9]+\/) ]]; then
    version="${d%?}"
    url="$url_base/$version/docs"
    dirs+=("<li><a href=\"$url\">Beadledom Docs Version $version</a></li>")
  fi
done

dirs+=('</ul>')

echo "Moving index.html to index.html.bak"

`mv index.html index.html.bak`

echo "Rebuilding index.html"

cat <<EOF > index.html
  <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  <html lang="en-US">
  <head>
    <meta charset="utf-8">
    <title>Beadledom Site Redirect</title>
    <meta http-equiv="refresh" content="5; url="${url}/docs"">
    <script type="text/javascript">
        window.location.replace("${url}/docs");
    </script>
  </head>
  <body>
    $(printf "%s\n" "${dirs[@]}")
  </body>
  </html>
EOF
