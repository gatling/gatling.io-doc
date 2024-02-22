import cf from "cloudfront";

const kvsId = "kvsId"
const kvsHandle = cf.kvs(kvsId);

const extensions = [".css", ".html", ".ico", ".js", ".json", ".png", ".svg", ".txt", ".xml", ".webmanifest", ".woff", ".woff2"];

function hasKnownFileExtension(uri) {
  for (const index in extensions) {
    const extension = extensions[index];
    if (uri.endsWith(extension)) {
      return true;
    }
  }
  return false;
}

function isEmpty(str) {
  return str === "" || str === null || str === undefined;
}

async function handler(event) {
  const request = event.request;
  const uri = request.uri;

  let location = null;
  if (uri.startsWith("/gatling") || uri.startsWith("/enterprise")) {
    const key = "/" + uri.split("/").filter(str => !isEmpty(str)).join("/");
    try {
      location = await kvsHandle.get(key);
    } catch (e) {
      console.log(`KVS key lookup failed for ${key}: ${e}`);
    }
  }

  if (location == null && !uri.endsWith("/") && !hasKnownFileExtension(uri)) {
    location = uri + "/";
  }

  if (location !== null) {
    return {
      statusCode: 301,
      statusDescription: "Moved Permanently",
      headers: {
        location: {
          value: location
        }
      }
    };
  }

  if (uri.endsWith("/")) {
    request.uri += "index.html";
  }

  return request;
}
