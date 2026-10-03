# Guidance for AI Agents

This file gives AI coding assistants and agents context for working with
meta-tegra on behalf of a user. Human contributors should read
[README.md](README.md) and [CONTRIBUTING.md](CONTRIBUTING.md) instead.

## Project context

* meta-tegra is an OpenEmbedded/Yocto BSP layer for NVIDIA Jetson modules.
  The supported Jetson Linux/JetPack release and boards are listed at the top
  of [README.md](README.md).
* Branches track Yocto releases. See
  [docs/Which-branch-should-I-use.md](docs/Which-branch-should-I-use.md) to
  match a user's setup to the right branch.
* Documentation is published at <https://oe4t.github.io> and its Markdown
  source is in the [docs](docs) directory. Check it before suggesting that
  something is a bug.
* [tegra-demo-distro](https://github.com/OE4T/tegra-demo-distro) is the
  reference distro. Settings such as `TEGRA_DEFAULT_KERNEL` come from it, not
  from meta-tegra. In meta-tegra the kernel is selected with
  `PREFERRED_PROVIDER_virtual/kernel`.

## Questions vs. issues

* Questions about getting started, general build setup, or how to use the
  layer belong in
  [GitHub Discussions](https://github.com/OE4T/meta-tegra/discussions), not
  in issues.
* Issues are for build or runtime problems with Tegra Yocto build targets.
  Before drafting an issue, search all of:
  * [existing issues](https://github.com/search?q=org%3AOE4T&type=issues)
    (`gh search issues --owner OE4T <terms>`)
  * [existing discussions](https://github.com/search?q=org%3AOE4T&type=discussions)
    (`gh search` does not cover discussions; use the web search or
    `gh api graphql` with a `search(type: DISCUSSION)` query)
  * the [NVIDIA developer forum](https://forums.developer.nvidia.com/c/robotics-edge-computing/jetson-systems/70)
    for Jetson Linux/JetPack, which often shows whether a problem also occurs
    with NVIDIA's stock BSP rather than only in meta-tegra
* Tell the user about any likely duplicates or relevant threads, and include
  links to them in the issue's Additional Context section.

## Filing an issue

`gh issue create` and the GitHub API do not apply issue templates, so follow
these steps yourself:

1. Use the structure of
   [.github/ISSUE_TEMPLATE/meta-tegra-bug-report.md](.github/ISSUE_TEMPLATE/meta-tegra-bug-report.md)
   for the issue body, and keep all of its sections.
2. Fill in the **AI tools used** field, naming the tool(s) and how they were
   used, as required by the
   [AI Usage Policy](CONTRIBUTING.md#ai-usage-policy).
3. Keep the description short: what the user did, what they expected, and
   what actually happened. Leave out background explanations, speculation
   about causes, and restated template guidance.
4. Only include reproduction steps the user actually ran. Do not invent,
   infer, or "clean up" steps, commands, or configuration values. If a
   required detail (branch, `MACHINE`, image, kernel provider) is unknown,
   ask the user rather than guessing.
5. Show the user the complete draft and have them review and approve it
   before submitting. The user is responsible for the report.

## Contributions

Follow the [AI Usage Policy](CONTRIBUTING.md#ai-usage-policy) for patches and
pull requests. In particular, add an `AI-Generated:` trailer naming the tool(s)
used, do not add a co-author line for the tool, and make sure the
`Signed-off-by:` is from the human developer.
