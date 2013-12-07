# Presto on Treasure Data

This is a simple guideline to contribute to the upstream Presto repository smoothly, and keep synchronized with them continuously.

## Preparation

1) clone treasure-data/presto:

```sh
$ git clone git@github.com:treasure-data/presto.git
$ cd presto
```

2) create `facebook` branch connected to facebook/presto:

```sh
$ git remote add facebook git@github.com:facebook/presto.git
$ git fetch facebook
$ git checkout -b facebook facebook/master
```


## Catching up with facebook repository

1) pull the latest change from `fracebook` branch:

```sh
$ git checkout facebook
$ git pull
```

2) merge changes to `master` branch:

```sh
$ git checkout master
$ git merge facebook
```


## Sending pull-request

1) merge changes to `master` branch

2) create a branch from the latest `facebook` branch with descriptive name:

```sh
$ git checkout facebook
$ git pull
$ git checkout -b "xyz-new-function"
```

3-a) if your changes consit of a few simple commits, cherry-pick them from `master` branch:

```sh
$ git cherry-pick <commit id 1>
$ git cherry-pick <commit id 2>
```

3-b) otherwise, create a patch using git-diff and apply it

```sh
$ git diff facebook...master presto-path/to/changed/file1 presto-path/to/changed/file2 > changes.patch
$ git apply changes.patch
$ git commit -a
```

5) push to github

```sh
$ git push -u origin "xyz-new-function"
```

6) send pull-request on github

https://github.com/treasure-data/td-hadoop/compare/xyz-new-function?expand=1

