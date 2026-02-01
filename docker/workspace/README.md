### Notes
#### Docker

```shell
docker build -t pow-dev:1.0
```
```shell
docker run -it --rm -v ./itools/config.yml:/root/.itools/config.yml -v ./powsybl-core:/workspace pow-dev:1.0
```
