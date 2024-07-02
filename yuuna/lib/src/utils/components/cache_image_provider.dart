import 'dart:ui';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

// ignore_for_file: deprecated_member_use

/// https://stackoverflow.com/questions/67963713/how-to-cache-memory-image-using-image-memory-or-memoryimage-flutter
/// https://gist.github.com/darmawan01/9be266df44594ea59f07032e325ffa3b
class CacheImageProvider extends ImageProvider<CacheImageProvider> {
  /// Make an [ImageProvider] that caches [MemoryImage].
  CacheImageProvider(this.tag, this.img);

  /// The cache id use to get cache.
  final String tag;

  /// The bytes of image to cache.
  final Uint8List img;

  @override
  ImageStreamCompleter loadBuffer(CacheImageProvider key, DecoderBufferCallback decode) {
    return MultiFrameImageStreamCompleter(
      codec: _loadAsync(decode),
      scale: 1,
      debugLabel: tag,
      informationCollector: () sync* {
        yield ErrorDescription('Tag: $tag');
      },
    );
  }

  Future<Codec> _loadAsync(DecoderBufferCallback decode) async {
    // the DefaultCacheManager() encapsulation, it get cache from local storage.
    final Uint8List bytes = img;

    if (bytes.lengthInBytes == 0) {
      // The file may become available later.
      PaintingBinding.instance.imageCache.evict(this);
      throw StateError('$tag is empty and cannot be loaded as an image.');
    }
    final ImmutableBuffer buffer = await ImmutableBuffer.fromUint8List(bytes);
    return decode(buffer);
  }

  @override
  Future<CacheImageProvider> obtainKey(ImageConfiguration configuration) {
    return SynchronousFuture<CacheImageProvider>(this);
  }

  @override
  bool operator ==(Object other) {
    if (other.runtimeType != runtimeType) {
      return false;
    }
    bool res = other is CacheImageProvider && other.tag == tag;
    return res;
  }

  @override
  int get hashCode => tag.hashCode;

  @override
  String toString() =>
      '${objectRuntimeType(this, 'CacheImageProvider')}("$tag")';
}
