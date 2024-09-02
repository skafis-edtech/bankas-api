# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
### Changed
### Fixed
### Removed

## 3.2.0 ...
Since firestore reads for free tier are pretty limited (50k reads per day), 
I have to do some optimization, so I don't need to pay money. That optimization 
includes refactoring Repository layer in code and using firebase realtimedb for caching frequently called volatile data. 
For now it won't be even volatile. Just will be used for reading and will be updated on every write or delete operation.
Later, when this will also exceed limits, then gonna limit the realtime db updating to a periodic stuff.

## 3.1.0 - 2024-08-27
Some stuff... not following...

## [1.0.0] - 2024-07-12

### Added
- Super admin access

### Changed
- Grouped by sources. Usable but unstable. Beta version

## [0.0.1] - 2024-07-03

### Added
- Initial unstable category grouped release

[unreleased]: https://github.com/naglissul/bankas-skafis-api/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/naglissul/bankas-skafis-api/compare/v1.0.0...v0.0.1
[0.0.1]: https://github.com/naglissul/bankas-skafis-api/releases/tag/v0.0.1
