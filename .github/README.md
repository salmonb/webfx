# WebFX [![Twitter URL](https://img.shields.io/twitter/url/https/twitter.com/WebFXProject.svg?style=social&label=WebFXProject)](https://twitter.com/WebFXProject)

WebFX lets Java developers target 7 platforms including the Web from a single application source code base.

<div align="center">

<table>
<tr>
<td><img src="html5.svg"/></td>
<td><img src="android.svg"/></td>
<td><img src="ios.svg"/></td>
<td><img src="windows.svg"/></td>
<td><img src="apple-dark.svg"/></td>
<td><img src="linux-dark.svg"/></td>
<td><img src="raspberry-pi.svg"/></td>
</tr>
</table>

</div>

For platforms other than the Web, the application will be native on 64-bit devices.

<div align="center">

| Platform                                           |          32-bit JRE           |          64-bit JRE           |         64-bit Native         |
|----------------------------------------------------|:-----------------------------:|:-----------------------------:|:-----------------------------:|
| Desktops (Windows, macOS & Linux)                  |               ✅               |               ✅               |               ✅               |
| Tablets & mobiles (Android & iOS)                  |               ❌               |               ❌               |               ✅               |
| Embedded (ex: Raspberry Pi) ~ *not yet documented* |               ✅               |               ✅               |               ✅               |
| Web (Chrome, FireFox, Edge, etc...)                | <img height=24 src="JS.svg"/> | <img height=24 src="JS.svg"/> | <img height=24 src="JS.svg"/> |

</div>

For the Web, WebFX will compile the application into a traditional self-contained pure JavaScript webapp (with no plugin or server required for its execution in the browser).

## Graphical demos

As mentioned above, the mobiles versions are not compatible with 32-bit devices. 

<div align="center">
<table>
<tr>

<td align="center" valign="bottom">
    <a href="https://tallycounter.webfx.dev">
        <img src="https://webfx-demos.github.io/webfx-demos-videos/TallyCounter.webp"/><br/>
        Tally Counter<br/><br/>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="browser-play-dark.svg"/>
          <img src="browser-play.svg"/>
        </picture>
    </a><br/>
    <a href="https://play.google.com/store/apps/details?id=dev.webfx.demo.tallycounter">
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="google-play-dark.svg"/>
          <img src="google-play.svg"/>
        </picture>
    </a>
    <a>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="app-store-dark.svg"/>
          <img src="app-store.svg"/>
        </picture>
    </a>
</td>

<td align="center" valign="bottom">
    <a href="https://moderngauge.webfx.dev">
        <img src="https://webfx-demos.github.io/webfx-demos-videos/ModernGauge.webp"/><br/>
        Modern Gauge<br/><br/>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="browser-play-dark.svg"/>
          <img src="browser-play.svg"/>
        </picture>
    </a><br/>
    <a href="https://play.google.com/store/apps/details?id=dev.webfx.demo.moderngauge">
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="google-play-dark.svg"/>
          <img src="google-play.svg"/>
        </picture>
    </a>
    <a>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="app-store-dark.svg"/>
          <img src="app-store.svg"/>
        </picture>
    </a>
</td>

<td align="center" valign="bottom">
    <a href="https://enzoclocks.webfx.dev">
        <img src="https://webfx-demos.github.io/webfx-demos-videos/EnzoClocks.webp"/><br/>
        Enzo Clocks<br/><br/>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="browser-play-dark.svg"/>
          <img src="browser-play.svg"/>
        </picture>
    </a><br/>
    <a href="https://play.google.com/store/apps/details?id=dev.webfx.demo.enzoclocks">
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="google-play-dark.svg"/>
          <img src="google-play.svg"/>
        </picture>
    </a>
    <a>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="app-store-dark.svg"/>
          <img src="app-store.svg"/>
        </picture>
    </a>
</td>

<td align="center" valign="bottom">
    <a href="https://spacefx.webfx.dev">
        <img src="https://webfx-demos.github.io/webfx-demos-videos/SpaceFX.webp"/><br/>
        SpaceFX<br/><br/>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="browser-play-dark.svg"/>
          <img src="browser-play.svg"/>
        </picture>
    </a><br/>
    <a href="https://play.google.com/store/apps/details?id=dev.webfx.demo.spacefx">
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="google-play-dark.svg"/>
          <img src="google-play.svg"/>
        </picture>
    </a>
    <a>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="app-store-dark.svg"/>
          <img src="app-store.svg"/>
        </picture>
    </a>
</td>

</tr>
<tr>

<td align="center" valign="bottom">
    <a href="https://demofx.webfx.dev">
        <img src="https://webfx-demos.github.io/webfx-demos-videos/DemoFX.webp"/><br/>
        DemoFX<br/><br/>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="browser-play-dark.svg"/>
          <img src="browser-play.svg"/>
        </picture>
    </a><br/>
    <a href="https://play.google.com/store/apps/details?id=dev.webfx.demo.demofx">
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="google-play-dark.svg"/>
          <img src="google-play.svg"/>
        </picture>
    </a>
    <a>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="app-store-dark.svg"/>
          <img src="app-store.svg"/>
        </picture>
    </a>
</td>

<td align="center" valign="bottom">
    <a href="https://raytracer.webfx.dev">
        <img src="https://webfx-demos.github.io/webfx-demos-videos/RayTracer.webp"/><br/>
        Ray Tracer<br/><br/>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="browser-play-dark.svg"/>
          <img src="browser-play.svg"/>
        </picture>
    </a><br/>
    <a href="https://play.google.com/store/apps/details?id=dev.webfx.demo.raytracer">
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="google-play-dark.svg"/>
          <img src="google-play.svg"/>
        </picture>
    </a>
    <a>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="app-store-dark.svg"/>
          <img src="app-store.svg"/>
        </picture>
    </a>
</td>

<td align="center" valign="bottom">
    <a href="https://mandelbrot.webfx.dev">
        <img src="https://webfx-demos.github.io/webfx-demos-videos/Mandelbrot.webp"/><br/>
        Mandelbrot<br/><br/>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="browser-play-dark.svg"/>
          <img src="browser-play.svg"/>
        </picture>
    </a><br/>
    <a href="https://play.google.com/store/apps/details?id=dev.webfx.demo.mandelbrot">
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="google-play-dark.svg"/>
          <img src="google-play.svg"/>
        </picture>
    </a>
    <a>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="app-store-dark.svg"/>
          <img src="app-store.svg"/>
        </picture>
    </a>
</td>

<td align="center" valign="bottom">
    <a href="https://fx2048.webfx.dev">
        <img src="https://webfx-demos.github.io/webfx-demos-videos/FX2048.webp"/><br/>
        FX2048<br/><br/>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="browser-play-dark.svg"/>
          <img src="browser-play.svg"/>
        </picture>
    </a><br/>
    <a href="https://play.google.com/store/apps/details?id=dev.webfx.demo.fx2048">
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="google-play-dark.svg"/>
          <img src="google-play.svg"/>
        </picture>
    </a>
    <a>
        <picture>
          <source media="(prefers-color-scheme: dark)" srcset="app-store-dark.svg"/>
          <img src="app-store.svg"/>
        </picture>
    </a>
</td>

</tr>

<tr>
<td colspan="4" align="center">
<a href="https://github.com/webfx-demos">
<img width="100%" src='MoreDemos.svg'/>
</a>
</td>
</tr>
</table>

</div>

## Enterprise demo (coming soon)

[Modality][modality] is the first real-world Enterprise-level WebFX application in development. Here are a few wireframes of the back-office (WebFX will provide all the components required for these UIs):

<table>
<tr>
<td><a href="https://modality.one/wireframes/Modality-wireframe-01.png"><img src="https://modality.one/wireframes/Modality-wireframe-01-thumbnail.png"/></a></td>
<td><a href="https://modality.one/wireframes/Modality-wireframe-02.png"><img src="https://modality.one/wireframes/Modality-wireframe-02-thumbnail.png"/></a></td>
<td><a href="https://modality.one/wireframes/Modality-wireframe-03.png"><img src="https://modality.one/wireframes/Modality-wireframe-03-thumbnail.png"/></a></td>
<td><a href="https://modality.one/wireframes/Modality-wireframe-04.png"><img src="https://modality.one/wireframes/Modality-wireframe-04-thumbnail.png"/></a></td>
</tr>
<tr>
<td><a href="https://modality.one/wireframes/Modality-wireframe-05.png"><img src="https://modality.one/wireframes/Modality-wireframe-05-thumbnail.png"/></a></td>
<td><a href="https://modality.one/wireframes/Modality-wireframe-06.png"><img src="https://modality.one/wireframes/Modality-wireframe-06-thumbnail.png"/></a></td>
<td><a href="https://modality.one/wireframes/Modality-wireframe-07.png"><img src="https://modality.one/wireframes/Modality-wireframe-07-thumbnail.png"/></a></td>
<td><a href="https://modality.one/wireframes/Modality-wireframe-08.png"><img src="https://modality.one/wireframes/Modality-wireframe-08-thumbnail.png"/></a></td>
</tr>
<tr>
<td><a href="https://modality.one/wireframes/Modality-wireframe-09.png"><img src="https://modality.one/wireframes/Modality-wireframe-09-thumbnail.png"/></a></td>
<td><a href="https://modality.one/wireframes/Modality-wireframe-10.png"><img src="https://modality.one/wireframes/Modality-wireframe-10-thumbnail.png"/></a></td>
<td><a href="https://modality.one/wireframes/Modality-wireframe-11.png"><img src="https://modality.one/wireframes/Modality-wireframe-11-thumbnail.png"/></a></td>
<td><a href="https://modality.one/wireframes/Modality-wireframe-12.png"><img src="https://modality.one/wireframes/Modality-wireframe-12-thumbnail.png"/></a></td>
</tr>
</table>

## How it works

WebFX is built on top of [OpenJFX](https://openjfx.io) and the [Gluon](https://gluonhq.com) toolchain that can already compile JavaFX applications to 6 platforms. WebFX is here to add the Web platform to this already great collection.

<p align="center">
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="https://docs.webfx.dev/webfx-readmes/webfx-cross-platform-dark.svg"/>
        <img src="https://docs.webfx.dev/webfx-cross-platform.svg" />
    </picture>
</p>

The heart of WebFX is a JavaFX application transpiler, which is mainly achieved with the [WebFX Kit](../webfx-kit) - a GWT compatible version of OpenJFX - making your application compilable to a Web App with [GWT][gwt-website]. For more explanation, please read the [documentation][webfx-docs].

<div align="center">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://docs.webfx.dev/webfx-readmes/webfx-kit-dark.svg">
      <img src="https://docs.webfx.dev/webfx-how-it-works.svg">
    </picture>
</div>

## Getting started

<table>
<tr>
<td align="center"><img src="tutorial1.png"/></td>
<td align="center"><img src="tutorial2.png"/></td>
<td align="center"><img src="tutorial3.png"/></td>
<td align="center"><img src="tutorial4.png"/></td>
<td align="center"><img src="tutorial5.png"/></td>
</tr>
<tr>
<td colspan="100" align="center">
<img width="100%" src='MoreVideoTutorials.svg'/>
</td>
</table>

Watch our video tutorials, or read our [guide to getting started][webfx-guide].

## Ecosystem

<div align="center">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://docs.webfx.dev/webfx-readmes/webfx-project-dark.svg">
      <img src="https://docs.webfx.dev/webfx-readmes/webfx-project-light.svg" />
    </picture>
</div>

Although the WebFX Kit (this repository) is the very heart of the project, there are other important repositories that together comprise the WebFX ecosystem. More info about them is given at the [organization level](https://github.com/webfx-project).

## Status

WebFX is still in the incubation phase. At this stage, we provide only snapshot releases, and breaking changes may occur until the first official release.

## Roadmap

You can consult our [roadmap](ROADMAP.md). Issues will be open when approaching the General Availability, and the project will have reached a more stable state.


## Keep updated [![Twitter URL](https://img.shields.io/twitter/url/https/twitter.com/WebFXProject.svg?style=social&label=WebFXProject)](https://twitter.com/WebFXProject)

You can follow us on [Twitter](https://twitter.com/WebFXProject) or subscribe to our [blog][webfx-blog] where we will post regular news and updates on the progress made.

## Get involved!

You can greatly help the project by:

- Following the [guide][webfx-guide] and start experimenting with WebFX
- Reporting any issues you may have with the [WebFX CLI][webfx-cli-repo], which we will try to fix
- Giving us feedback in our GitHub [discussions][webfx-discussions]

You want to get involved in the development as well? You are very welcome! Please read our [contributing guide](CONTRIBUTING.md).

## License

WebFX is an open-source software licensed under the [Apache License 2.0](../LICENSE).

## Is it free?

Yes WebFX itself is free, and you can build Web apps and Desktop apps (with embed JRE) for free. But if you target mobiles & native apps, you will need to acquire a [Gluon Mobile license](https://gluonhq.com/pricing/) to remove the nag screen.

## Funding

WebFX is a project sponsored by NKT-IKBU for the development of its booking system [Modality][modality].

[webfx-website]: https://webfx.dev
[webfx-docs]: https://docs.webfx.dev
[webfx-demos]: https://github.com/webfx-demos
[webfx-guide]: https://docs.webfx.dev/#_getting_started
[webfx-blog]: https://blog.webfx.dev
[webfx-discussions]: https://github.com/webfx-project/webfx/discussions
[webfx-contact]: mailto:maintainer@webfx.dev
[webfx-cli-repo]: https://github.com/webfx-project/webfx-cli
[gwt-website]: http://www.gwtproject.org
[modality]: https://github.com/modalityone/modality