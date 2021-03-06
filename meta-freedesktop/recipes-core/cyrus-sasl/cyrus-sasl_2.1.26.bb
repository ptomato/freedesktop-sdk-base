DESCRIPTION = "Generic client/server library for SASL authentication."
SECTION = "console/network"
DEPENDS = "openssl virtual/db"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f55e0974e3d6db00ca6f57f2d206396"

SRC_URI = "ftp://ftp.cyrusimap.org/cyrus-sasl/cyrus-sasl-${PV}.tar.gz \
	file://debian_patches_0009_sasldb_al.diff \
	file://debian_patches_0014_avoid_pic_overwrite.diff \
	"

INSANE_SKIP_${PN} += "dev-so"

inherit autotools pkgconfig

EXTRA_OECONF += "--with-dblib=berkeley \
                 --with-bdb-libdir=${STAGING_LIBDIR} \
                 --with-bdb-incdir=${STAGING_INCDIR} \
                 --without-pam --without-opie --without-des \
		 --disable-gssapi \
                 andrew_cv_runpath_switch=none"

do_configure_prepend () {
    rm -f ${S}/acinclude.m4 ${S}/config/libtool.m4
}

do_compile_prepend () {
    (cd ${B}/include; \
    ${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} ${S}/include/makemd5.c -o makemd5; \
    touch makemd5.o makemd5.lo makemd5;\
    )
}

pkg_postinst_${PN}-bin () {
    grep cyrus /etc/passwd || adduser --disabled-password --home=/var/spool/mail --ingroup mail -g "Cyrus sasl" cyrus
    echo "cyrus" | saslpasswd2 -p -c cyrus
    chgrp mail /etc/sasldb2
}

SRC_URI[md5sum] = "a7f4e5e559a0e37b3ffc438c9456e425"
SRC_URI[sha256sum] = "8fbc5136512b59bb793657f36fadda6359cae3b08f01fd16b3d406f1345b7bc3"

PACKAGES =+ "${PN}-bin"

FILES_${PN}           += "${libdir}/sasl2/*.so*"
FILES_${PN}-bin       += "${bindir}"
FILES_${PN}-dev       += "${libdir}/sasl2/*.la"
FILES_${PN}-dbg       += "${libdir}/sasl2/.debug"
FILES_${PN}-staticdev += "${libdir}/sasl2/*.a"
