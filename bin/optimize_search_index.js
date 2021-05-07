const FlexSearch = require("flexsearch");
const fs = require('fs');
const _ = require('lodash');

// IMPORTANT: Has to be the same as theme configuration (e.g: assets/js/index.ts), but not async
const indexOptions = {
  filter: false,
  cache: true,
  encode: "extra",
  tokenize: "strict",
  doc: {
    id: 'id',
    field: [
      'title',
      'description',
      'content',
      'section',
      'version',
      'latest'
    ],
    // @ts-ignore https://github.com/nextapps-de/flexsearch/issues/152
    store: [
      'title',
      'description',
      'href'
    ]
  },
}

const index = FlexSearch.create(indexOptions)

const handleError = (message, error) => {
  if (error) {
    console.error(message, error);
    process.exit(1);
  }
}

const [path, encoding] = process.argv.slice(2);

fs.readFile(path, encoding, (error, data) => {
  handleError("Error while reading index file", error);
  const search = JSON.parse(data);
  index.add(search.indexes);

  const compiledSearch = {
    exported: true,
    indexes: index.export({ serialize: false })
  }

  fs.writeFile(path, JSON.stringify(compiledSearch), (error) => {
    handleError("Error while exporting indexes", error)
  });
});
