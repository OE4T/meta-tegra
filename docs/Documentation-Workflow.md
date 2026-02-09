# Documentation Workflow

This project uses [mdBook](https://rust-lang.github.io/mdBook/) to generate
documentation, with GitHub Actions for automated builds and GitHub Pages for
hosting.

## Repository Layout

Documentation source files live alongside the Yocto BSP layer content:

```
meta-tegra/
├── book.toml                      # mdBook configuration
├── docs/                          # Documentation source (markdown)
│   ├── SUMMARY.md                 # Table of contents for mdBook
│   ├── README.md                  # Introduction / landing page
│   ├── *.md                       # Documentation pages
│   └── mdbook/                    # Custom mdBook assets
│       ├── css/custom.css         # Version dropdown styling
│       └── js/version-dropdown.js # Version switching logic
└── .github/workflows/
    └── mdbook-versioned.yml       # CI/CD workflow
```

The `book.toml` in the repository root configures mdBook. The `src` setting
points to the `docs/` directory, and custom CSS and JavaScript are loaded for
the version dropdown:

```toml
[book]
title = "OE4T Meta Tegra"
authors = ["Matt Madison", "Dan Walkes"]
language = "en"
src = "docs"

[output.html]
additional-css = ["docs/mdbook/css/custom.css"]
additional-js = ["docs/mdbook/js/version-dropdown.js"]
```
## Multi-Version Support

Each tracked branch gets its own independent copy of the documentation on GitHub
Pages. For the list of branches tracked, refer to the versions.json file in
the `gh-pages` branch of this repository.

## Adding Pages

All documentation pages are Markdown files in the `docs/` directory. To add a
new page:

1. Create a new `.md` file in `docs/`.
2. Add an entry for it in `docs/SUMMARY.md`. The SUMMARY file defines the table
   of contents and sidebar navigation. Pages not listed in SUMMARY.md will not
   appear in the built documentation.

## Page Editing Tips

* Please ensure any embedded links to other documentation files are done with relative
paths.  For example, use `[Link to another page in docs](OtherPageName.md)` instead
of `[Link to another page in docs](https://github.com/OE4T/meta-tegra/blob/master/docs/OtherPageName.md)`
* You can use the trick at [this stackoverflow post](https://stackoverflow.com/a/26601810) to add
images to your markdown file without the need to check images into the repo.

## Preview Locally

To preview the documentation locally with markdown, install
[mdBook](https://rust-lang.github.io/mdBook/guide/installation.html) and run:

```sh
mdbook serve
```

This starts a local web server with live reloading as you edit files.


### Build and Deploy

The GitHub Actions workflow (`.github/workflows/mdbook-versioned.yml`) triggers
on pushes to tracked branches:

1. **Build** — runs `mdbook build` inside a `peaceiris/mdbook` container,
   producing output in a per-branch directory.
2. **Deploy** — pushes the built HTML to a subdirectory on the `gh-pages`
   branch using `peaceiris/actions-gh-pages`.

Each branch deploys to its own directory, resulting in this structure on the
`gh-pages` branch:

```
gh-pages/
├── index.html          # redirects to ./master/
├── versions.json       # lists available versions for the dropdown
├── master/             # docs built from the master branch
└── scarthgap/          # docs built from the scarthgap branch
```

The workflow can also be triggered manually via `workflow_dispatch` from the
GitHub Actions UI.

### Version Dropdown

A custom JavaScript file (`docs/mdbook/js/version-dropdown.js`) adds a version
selector dropdown to the mdBook navigation bar. It fetches `versions.json` from
the site root to populate the list, and when a different version is selected it
navigates to the same page path under the new version's directory.

The `versions.json` file is maintained manually on the `gh-pages` branch (not
auto-generated), giving explicit control over which versions appear in the
dropdown.

### Adding a New Version

To add documentation for a new branch (e.g., `kirkstone`):

1. Add the branch name to the `on.push.branches` list in
   `.github/workflows/mdbook-versioned.yml`.
2. Push content to that branch. The workflow will automatically build and deploy
   to a new directory on `gh-pages`.
3. Update `versions.json` on the `gh-pages` branch to include the new entry so
   it appears in the version dropdown.
