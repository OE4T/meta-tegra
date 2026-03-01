Thank you for contributing to the OE4T project!  Your contributions are greatly
appreciated!


Submitting Code Changes
------------

The OE4T project repositories follow the [OpenEmbedded
Guidelines](https://www.openembedded.org/wiki/Commit_Patch_Message_Guidelines#Patch_Headers_and_Commit_Messages).
Please review these when proposing your Pull Request.  A few highlights and
additional requirements:
* Please submit issues or pull requests through Github.  Only rebase and squash
commits are used for PRs, so if you have a PR that is outstanding for a long
time, please keep your branch up to date by rebasing your changes, rather than
merging.
* Group commits based on their functionality and components changed.  For the
first line, use something like `component: Short Summary` to describe your
change where `component` refers to a specific software component being changed.
* Please try to make incremental changes with multiple commits, rather than "big
bang" single commits with changes spread across multiple components.
* Add a `Signed-off-by:` line to your commit, using `git commit -s` or a
pre-commit hook like the one setup with [this script](https://github.com/OE4T/tegra-demo-distro/blob/master/scripts-setup/setup-git-hooks),
using your real name and e-mail address (no anonymous contributions, please).
This indicates that you have the right to submit the patch per the
Developer's Certificate of Origin in the next section.
* Target the master branch for pull requests unless your change is specific
to an earlier branch.

## Developer's Certificate of Origin
By making a contribution to this project, I certify that:
1. The contribution was created in whole or in part by me and I have the right
to submit it under the open source license indicated in the file; or
2. The contribution is based upon previous work that, to the best of my
knowledge, is covered under an appropriate open source license and I have the
right under that license to submit that work with modifications, whether
created in whole or in part by me, under the same open source license
(unless I am permitted to submit under a different license),
as indicated in the file; or
3. The contribution was provided directly to me by some other person who
certified (1), (2) or (3) and I have not modified it.
4. I understand and agree that this project and the contribution are public and
that a record of the contribution (including all personal information I submit
with it, including my sign-off) is maintained indefinitely and may be
redistributed consistent with this project or the open source license(s)
involved.

(Adapted from the [Linux kernel's certificate of origin](https://www.kernel.org/doc/html/latest/process/submitting-patches.html#developer-s-certificate-of-origin-1-1).)


Submitting Documentation Changes
------------

Documentation is served as an mdbook based on the content in the docs
directory.  Please open a PR for documentation changes on the relevant
branch.  Please target the master branch for docs changes
unless your changes are specific to older branches.  Documentation content
in older branches are based on a snapshot at branch time and may be out of
date.
